# MineCamera

`MineCamera` 是一个面向 `Minecraft Java 1.21.11` 的 `Fabric` 相机模组。

## 版本基线

- Minecraft：`1.21.11`
- Fabric Loader：`0.19.3`
- Fabric API：`0.141.4+1.21.11`
- Java：`21`

本仓库按 `Fabric Loader 0.19.3` 进行开发和验证，不按“最新版 Fabric 通用兼容”宣传。

## v1.0 已实现功能

- 手持相机 HUD 与实时取景
- 三脚架放置、挂载相机与独立机位拍摄
- 将游戏画面拍摄为本地 `PNG`
- 空白胶卷 / 已曝光胶卷流程
- HUD 内照片回放
- 照片展示框与自动拼组大图展示
- 分辨率预设与焦距调节
- 本地媒体索引与客户端图片回放
- `Sodium / Iris` 的基础兼容处理

## v1.0 暂未实现

- 真实视频录制与导出
- 最终版景深 / 光圈虚化成像
- 最终版自动对焦实现
- 三脚架顶部独立相机小模型终稿

## 安装说明

1. 安装 `Minecraft Java 1.21.11`
2. 安装 `Fabric Loader 0.19.3`
3. 安装 `Fabric API`
4. 将构建好的 `minecamera` jar 放入 `mods` 文件夹
5. 使用 `Java 21` 启动游戏

## 构建方法

```bash
./gradlew build
```

构建完成后的 jar 位于 `build/libs/`。

## 使用说明

- 详细中文用法文档：[docs/USAGE.zh-CN.md](docs/USAGE.zh-CN.md)
- 快速操作摘要：
  - 手持相机 `右键` 打开 HUD
  - 取景模式下 `左键` 拍照
  - `右键` 关闭相机
  - `Tab` 切换分辨率预设
  - `鼠标滚轮` 调整焦距
  - `R` 切换取景 / 回放
  - 回放模式下 `A / D` 切换上一张 / 下一张

## 开源协议

本项目采用 `MIT` 协议开源，详见 [LICENSE](LICENSE)。
