# MineCamera v0.2.1 版本开发与功能记录

## 1. 版本基本信息

- **版本号**：`v0.2.1`
- **对应构建产物**：`release/minecamera-0.2.1.jar`
- **支持环境**：`Minecraft Java 1.21.11` + `Fabric Loader 0.19.3` + `Java 21`
- **版本定位**：手持相机移动平滑插值修复、快门流畅体验重构、光影兼容逻辑还原与分辨率预设分级。

---

## 2. 具体工作与代码修改（干了什么、更新了什么、修复了什么）

### 2.1 修复手持相机移动时画面“小幅度抽搐”（核心体验修复）
- **修改文件**：
  - `src/client/java/com/minecamera/client/mixin/GameRendererMixin.java`
  - `src/client/java/com/minecamera/client/camera/MineCameraModeController.java`
- **问题根因**：
  在 `GameRendererMixin.updateCamera` 注入点，代码在末尾通过 `accessor.minecamera$setPos(snapshot.position())` 强制覆盖了相机的坐标和朝向。而在手持模式下，该位置取的是 `player.getEyePos()`（这是未经过渲染帧时间差插值的 **20 TPS 物理 Tick 离散坐标**）。原版 Minecraft 在 60/144+ FPS 下运行的子刻平滑插值（Sub-tick Interpolation）被彻底摧毁，导致玩家在取景状态下行走或转动视角时，画面每秒产生 20 次离散位移抽搐。
- **改动措施**：
  1. 在 `GameRendererMixin.minecamera$applyOffscreenCameraOverride` 中增加条件分支：**手持取景（LIVE）状态下完全不干预相机的坐标与旋转**，彻底交回原版平滑插值系统处理；仅在挂载三脚架时或处于离屏渲染截帧瞬间才接管机位。
  2. 在 `MineCameraModeController.buildCurrentSnapshot` 中，拍摄快照优先从已就绪的渲染相机读取平滑插值坐标 `client.gameRenderer.getCamera().getCameraPos()` 以及 `getYaw()` / `getPitch()`，保证移动或跳跃中拍摄到的相片与画面所见像素级精确同步。

### 2.2 消除“已保存”提示弹出瞬间的 0.1 秒微卡顿（性能与体验修复）
- **修改文件**：`src/client/java/com/minecamera/client/camera/MineCameraModeController.java`
- **问题根因**：
  在后台异步写盘完成后，主线程会收到 `onCaptureSucceeded(fileName)` 回调。原代码在该回调中急切（Eagerly）执行了 `REVIEWS.refresh(...)`，导致刚刚保存到磁盘的超大 PNG 图片被立即在主线程读取、解码为 `NativeImage` 并通过 OpenGL 注册上传显存。而在 LIVE 取景模式下，屏幕上只绘制十字准星与取景黑框，这个回放纹理根本不在屏幕上绘制，造成了毫无意义的主线程 0.1 秒微卡顿。
- **改动措施**：
  在 `onCaptureSucceeded` 中增加前置判定：仅当玩家当前正处于回放模式（`CameraPreviewMode.REVIEW`）时才刷新纹理；处于实时取景（`LIVE`）模式时，仅更新最新拍摄 ID 与状态提示文本，将回放纹理的解码与显卡上传延迟至玩家主动按下 `R` 键进入回放时按需加载。

### 2.3 彻底优化快门 0.5 秒硬性停顿与重构预设阶梯（性能重构）
- **修改文件**：
  - `src/main/java/com/minecamera/logic/ResolutionPresets.java`
  - `src/main/java/com/minecamera/logic/CameraModePresetSet.java`
  - `src/test/java/com/minecamera/logic/ResolutionValidatorTest.java`
- **问题根因**：
  旧版中 `CameraModePresetSet.PRESETS` 里的 5 个预设（1:1、4:3、3:2、16:9、21:9）**全部被 `maxPreset` 强制拉满到了 4K 极限画质（约 830 万像素）**：
  - 1:1 是 `2880x2880`（829 万像素）
  - 16:9 是 `3840x2160`（829 万像素）
  - 4:3 是 `3324x2493`（828 万像素）
  
  因此玩家无论怎么按 `Tab` 切换预设，所有预设像素量全部是 4K。按下快门瞬间，游戏主线程必须在离屏 Framebuffer 完整重绘一次全景世界（包括光影阴影 pass 与所有后处理），再把 33MB 显存读回 CPU 并执行 829 万次循环，导致了无法避免的约 0.5 秒硬性冻结。
- **改动措施**：
  1. 在 `ResolutionPresets` 中新增 `standardPreset`，引入标准 1080p 摄影基准：
     - `16:9` 为 `1920x1080`（207 万像素）
     - `4:3` 为 `1440x1080`（155 万像素）
     - `1:1` 为 `1080x1080`（116 万像素）
     - `3:2` 为 `1620x1080`（175 万像素）
     - `21:9` 为 `2520x1080`（272 万像素）
  2. 1080p 标准预设像素总量仅为原本 4K 强制拉满的 **25%**，离屏渲染与显存回读开销暴降 75%，快门卡顿降至毫秒级无感体验。
  3. 在预设轮换最后保留了 `16:9 4K`（`3840x2160`）与 `1:1 4K`（`2880x2880`）两个超高画质档位，供需要打印大幅海报的场景按需选用。

### 2.4 回退 Iris 光影的冗余修改（代码还原）
- **修改文件**：`src/client/java/com/minecamera/client/mixin/IrisHandRendererMixin.java`
- **改动措施**：
  彻底回滚了上一版加入的 `|| MineCameraModeController.isActive()` 冗余拦截，恢复至与 v0.1.2 严格一致的判断逻辑：`if (MineCameraCaptureRenderContext.shouldSuppressHandRendering()) { ci.cancel(); }`。

---

## 3. 测试与验证

- 执行 `./gradlew.bat test`，共 **167 项单元测试全部通过（100% Pass）**（包含新增的 `standardPreset` 尺寸与有效性测试）。
- 执行 `./gradlew.bat build`，成品成功编译并归档至 `release/minecamera-0.2.1.jar`。
