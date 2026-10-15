# MineCamera v0.2.0 版本开发与功能记录

## 1. 版本基本信息

- **版本号**：`v0.2.0`
- **对应构建产物**：`release/minecamera-0.2.0.jar`
- **支持环境**：`Minecraft Java 1.21.11` + `Fabric Loader 0.19.3` + `Java 21`
- **版本定位**：核心逻辑安全性审计、稳定性增强、后台异步化与工程体系规范化。

---

## 2. 具体工作与代码修改（干了什么、更新了什么、修复了什么）

### 2.1 服务端网络线程安全性修复（修复）
- **修改文件**：`com/minecamera/network/MineCameraNetworking.java`
- **问题根因**：原版 Fabric 网络回调运行于 Netty I/O 线程，直接在 Netty 线程内操作玩家背包（如扣除胶卷、给予已曝光胶卷）和世界方块实体，极易引发并发修改异常（`ConcurrentModificationException`）以及玩家卡刷物品漏洞。
- **改动措施**：将所有 C2S 数据包（`CommitPhotoCapturePayload`、`RequestDisplayThumbnailPayload`）的执行体全部包裹在 `context.server().execute(() -> { ... })` 中，保证操作 100% 运行在 Minecraft 游戏主线程。

### 2.2 拍照超时未提交文件自动垃圾回收（修复）
- **修改文件**：`com/minecamera/client/capture/MineCameraCaptureCoordinator.java`、`CaptureCommitSession.java`
- **问题根因**：拍照生成本地 PNG 后需向服务端提交验证。若由于网络波动、服务端报错或玩家断开连接导致超时，本地已生成的 PNG 仍会残留在磁盘上，长期使用会导致磁盘垃圾堆积。
- **改动措施**：引入 `CaptureCommitSession` 会话管理，增加 15 秒超时判定与 `onDisconnected` 监听钩子。一旦发生超时或断连，自动调用 `ClientCaptureService.deleteCapture(...)` 清理未提交的本地图片与索引。

### 2.3 相册索引解析改用标准 Gson 序列化（重构与健壮性提升）
- **修改文件**：`com/minecamera/media/MediaIndexStore.java`
- **测试补充**：`com/minecamera/media/MediaIndexStoreTest.java`
- **改动措施**：彻底废弃原本脆弱的正则表达式 JSON 文本匹配方案，改用 Google `Gson` 库进行标准化类型安全序列化与反序列化。补充了多组单元测试，确保能够稳健处理玩家名包含特殊转义字符、字段乱序以及中文字符等复杂边界。

### 2.4 快门图片保存与缩略图生成异步化（性能优化）
- **修改文件**：`com/minecamera/client/capture/ClientCaptureService.java`
- **改动措施**：将高分辨率 PNG 压缩写入（`image.writeTo(finalPath)`）、缩略图计算（`DisplayThumbnailEncoder.encode`）和本地索引更新全部移至原版后台线程池 `net.minecraft.util.Util.getIoWorkerExecutor()` 执行。主渲染线程在读取显存像素后立即释放 Framebuffer，避免主线程因长达 1 秒的文件 I/O 产生冻结。

### 2.5 展示框未就绪缓存冷却保护（性能优化）
- **修改文件**：`com/minecamera/client/media/ClientPhotoTextureCache.java`
- **改动措施**：为未下载完成或缺失的照片纹理增加 3 秒读取冷却缓存（`MISSING_COOLDOWN_MILLIS`），避免每个相框在每帧渲染时（60/144 FPS）持续高频向本地磁盘发起读取探测。

### 2.6 消除高频交互反射开销（性能优化）
- **修改文件**：`com/minecamera/item/CameraItem.java`、`com/minecamera/world/block/TripodBlock.java`、新增 `com/minecamera/client/CameraClientHooks.java`
- **改动措施**：删除了右键相机和三脚架时通过 `Class.forName("com.minecamera.client...")` 进行的反射调用，改用接口解耦的客户端专用钩子类，消除了反射查找性能消耗。

### 2.7 规范化工程体系建立
- 确立了多版本文件夹共存架构（`MineCamera v<version>`）。
- 建立了项目根目录 `release/` 产物自动归档体系。
- 编制了核心开发规约 `GEMINI.md` 与专用 Agent 技能 `minecamera-dev`。

---

## 3. 测试与验证

- 执行 `./gradlew.bat test`，共 **166 项单元测试通过（100% Pass）**。
- 成品已归档至 `release/minecamera-0.2.0.jar`。

---

## 4. 该版本遗留并在 v0.2.1 修复的问题

1. **手持移动时画面小幅度抖动抽搐**：`GameRendererMixin` 的 `updateCamera` 注入点强制覆盖了玩家相机位置与旋转，覆盖的为未插值的 20 TPS Tick 坐标，破坏了原版子刻平滑插值。
2. **快门瞬间卡顿约 0.5 秒**：`CameraModePresetSet` 内全部预设均强制拉满为 4K（约 830 万像素），主线程离屏重绘与显存回读开销过大。
3. **“已保存”跳出时微卡 0.1 秒**：异步写盘完成后主线程立即解压并上传了当前不需要绘制的回放纹理。
4. **Iris 手部遮挡冗余判定**：在 `IrisHandRendererMixin` 中添加了非必要的 `MineCameraModeController.isActive()` 判定。
