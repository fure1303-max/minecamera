# MineCamera

`MineCamera` 是一个面向 `Minecraft Java 1.21.11` 的 `Fabric` 相机模组，当前版本以“能在游戏内稳定拍照、回放、保存本地 PNG”为主线。

仓库地址：[github.com/fure1303-max/minecamera](https://github.com/fure1303-max/minecamera)  
发布页：[Releases](https://github.com/fure1303-max/minecamera/releases)

## 下载最新版本

- 最新发布页：[MineCamera Releases](https://github.com/fure1303-max/minecamera/releases)
- 当前最新版本：[v0.2.2](https://github.com/fure1303-max/minecamera/releases/tag/v0.2.2)
- 下载后请使用 `Minecraft 1.21.11 + Fabric Loader 0.19.3 + Java 21` 运行

## 版本要求

- Minecraft：`1.21.11`
- Fabric Loader：`0.19.3`
- Fabric API：`0.141.4+1.21.11`
- Java：`21`

本仓库按上面这组版本开发和验证，不按“任意最新版 Fabric 都兼容”宣传。

## 当前已实现

- 手持相机 HUD 与实时取景（完整保留 60/144+ FPS 子刻平滑插值，无移动抽搐）
- 三脚架放置、挂载相机、独立机位拍摄
- 离屏拍照并保存本地 `PNG`（后台线程池异步压缩与缩略图计算，快门无卡顿）
- 空白胶卷 / 已曝光胶卷流程
- HUD 内照片回放（按需解码显存，无预载卡顿）
- 分辨率预设切换（含标准 1080p 摄影预设与 16:9 / 3:2 / 1:1 4K 极限画质预设）
- 焦距调节（10mm ~ 600mm 无级变焦）
- 本地媒体索引与客户端图片回放（采用类型安全 Gson 解析）
- `Sodium / Iris` 的基础兼容处理
- 照片展示框单框显示、多框矩形自动拼图与照片宽高比裁切
- 展示框空手右键取下相片并返还对应胶卷（完整还原拍摄作者、时间、焦距等元数据）
- 展示框贴照片胶卷消耗、旧相片防吞回收与方块拆除防丢掉落机制
- 展示框增删后的稳定重组、照片继承和缺失照片占位
- 房主局域网缩略图同步，客户端不传输原始 PNG

## 当前未实现

- 真实视频录制与导出
- 最终版景深 / 光圈虚化成像
- 最终版自动对焦
- 三脚架顶部独立相机小模型终稿

## 安装

1. 安装 `Minecraft Java 1.21.11`
2. 安装 `Fabric Loader 0.19.3`
3. 安装 `Fabric API`
4. 使用 `Java 21` 启动游戏
5. 将 `minecamera` 的 jar 放入 `mods` 文件夹

## 构建

```bash
./gradlew build
```

构建产物默认在 `build/libs/`，归档于根目录 `release/`。

## 文档

- [变更记录](MineCamera%20v0.2.2/CHANGELOG.md)
- [v0.2.2 版本开发与功能记录](MineCamera%20v0.2.2/VERSION_NOTES.md)
- [v0.2.1 版本开发与功能记录](MineCamera%20v0.2.1/VERSION_NOTES.md)
- [v0.2.0 版本开发与功能记录](MineCamera%20v0.2.0/VERSION_NOTES.md)
- [v0.1.2 版本开发与功能记录](MineCamera%20v0.1.2/VERSION_NOTES.md)
- [模组开发规约与工作流指南](GEMINI.md)

## 开源协议

本项目采用 [MIT](LICENSE) 协议开源。
