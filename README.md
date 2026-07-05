# MineCamera

`MineCamera` 是一个面向 `Minecraft Java 1.21.11` 的 `Fabric` 相机模组，当前版本以“能在游戏内稳定拍照、回放、保存本地 PNG”为主线。

仓库地址：[github.com/fure1303-max/minecamera](https://github.com/fure1303-max/minecamera)  
发布页：[Releases](https://github.com/fure1303-max/minecamera/releases)

## 版本要求

- Minecraft：`1.21.11`
- Fabric Loader：`0.19.3`
- Fabric API：`0.141.4+1.21.11`
- Java：`21`

本仓库按上面这组版本开发和验证，不按“任意最新版 Fabric 都兼容”宣传。

## 当前已实现

- 手持相机 HUD 与实时取景
- 三脚架放置、挂载相机、独立机位拍摄
- 离屏拍照并保存本地 `PNG`
- 空白胶卷 / 已曝光胶卷流程
- HUD 内照片回放
- 分辨率预设切换
- 焦距调节
- 本地媒体索引与客户端图片回放
- `Sodium / Iris` 的基础兼容处理
- 照片展示框物品与基础放置形态

## 当前未实现

- 真实视频录制与导出
- 最终版景深 / 光圈虚化成像
- 最终版自动对焦
- 照片展示框的照片装入、照片展示与自动拼组大图展示
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

构建产物默认在 `build/libs/`。

## 文档

- [中文使用说明](docs/USAGE.zh-CN.md)
- [GitHub 开源与发版说明](docs/GITHUB-OPEN-SOURCE.zh-CN.md)
- [变更记录](CHANGELOG.md)

## 仓库结构

- `src/main/java`：服务端共用逻辑、物品、方块、数据结构
- `src/client/java`：客户端 HUD、回放、离屏渲染、输入与兼容逻辑
- `src/main/resources`：模组资源、语言文件、模型、配方、`fabric.mod.json`
- `src/test/java`：当前核心逻辑和资源层的测试
- `docs/`：玩家文档与仓库维护文档

## 反馈问题

如果你想反馈 bug，优先附上这些信息：

- 你使用的 Minecraft / Fabric Loader / Java 版本
- 是否安装了 `Sodium`、`Iris` 或 shader pack
- `latest.log`
- 对应时间点的崩溃报告或截图

## 开源协议

本项目采用 [MIT](LICENSE) 协议开源。
