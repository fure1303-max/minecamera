---
name: minecamera-dev
description: Guide and runbooks for developing, testing, versioning, and releasing MineCamera (a Minecraft 1.21.11 Fabric camera mod). Use when modifying MineCamera code, creating a new mod version folder, building/testing the mod, or preparing a GitHub Release.
---

# MineCamera 模组开发与发布技能指南

`MineCamera` 是一个面向 `Minecraft Java 1.21.11` 的 `Fabric` 相机模组，支持手持与三脚架机位拍照、胶卷流程、HUD 实时取景与回放、照片展示框多框拼接、离屏渲染以及局域网缩略图同步。

## 1. 项目结构与多版本管理准则

工作区遵循“多版本文件夹共存”规范，每个版本都在根目录拥有单独文件夹，并在根目录设立统一的 `release` 归档：
```text
d:\Gemini\MineCamera\
├── GEMINI.md
├── .agents/skills/minecamera-dev/SKILL.md
├── release/                    # 统一存放所有编译归档的 release jar
│   └── minecamera-0.1.2.jar
├── MineCamera v0.1.2/          # 当前基线版本
└── MineCamera v<新版本号>/     # 后续新版本
```

### 开启新版本流程 Runbook

当用户要求开始开发新版本（例如 `v0.1.3`）时，按以下步骤执行：
1. **复制文件夹**：
   将上一版本的目录（如 `MineCamera v0.1.2`）递归复制到新版本文件夹（如 `MineCamera v0.1.3`）。注意排除 `.gradle`、`build`、`.idea` 等临时产物目录。
2. **更新版本号**：
   在新文件夹的 `gradle.properties` 中修改：
   ```properties
   mod_version=0.1.3
   ```
3. **维护版本开发文档（强制）**：
   在新版本目录中创建并持续维护 `VERSION_NOTES.md`，详尽记录该版本开发所做的工作（干了什么）、更新了什么、修复了什么（包含缺陷根因与修复方案），以及测试验证情况。
4. **更新变更日志**：
   在 `CHANGELOG.md` 顶部插入新版本的 `## [新版本] - YYYY-MM-DD` 结构，列出面向用户的简要更新条目。
5. **在新目录中进行开发**：
   后续所有代码修改、编译、测试均在新目录内执行。

## 2. 编译、测试与 release 归档 Runbook

### 本地环境配置
- **JDK 路径**：`C:\Users\FuRE\AppData\Roaming\.minecraft\runtime\java-runtime-delta` (Java 21)
- **执行命令模板**：
```powershell
$env:JAVA_HOME = "C:\Users\FuRE\AppData\Roaming\.minecraft\runtime\java-runtime-delta"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
cd "d:\Gemini\MineCamera\MineCamera v<version>"

# 运行单元测试
.\gradlew.bat test --no-parallel

# 构建发布 jar
.\gradlew.bat build --no-parallel

# 归档到根目录 release/
Copy-Item "build\libs\minecamera-<version>.jar" "..\release\minecamera-<version>.jar" -Force
```

> **提示**：如果网络连接 Fabric Maven 较慢或不稳定，建议使用 `--no-parallel` 避免并发 SSL 握手重置。
> 构建产物输出位于：`build/libs/minecamera-<version>.jar`，构建完毕后自动复制到 `release/` 归档。

## 3. 发版与 GitHub Releases 流程 Runbook

### 双分支协同与发布流水线（每产出新版本必须执行）：
1. **工作台归档与自用备份（`refactor/multi-version` 分支）**：
   - 在多版本平铺工作区完成编码与测试后，生成归档 jar（`release/minecamera-<version>.jar`）；
   - 更新对应版本子目录的 `VERSION_NOTES.md` 与根目录 `README.md`；
   - 自动提交并推送到内部全景分支 `refactor/multi-version`。
2. **正统主分支同步与发版（`main` 分支）**：
   - 将新版本的纯净源码（`src/`、`build.gradle`、`README.md` 等）同步提交至 `main` 主分支，推送到 GitHub 远端；
   - 在 `main` 对应提交上创建并推送版本 Tag：`v<version>`（如 `v0.2.3`）；
   - 在 GitHub Releases 发布新版本，上传 `release/minecamera-<version>.jar` 并附带更新明细。

## 4. Git 版本控制与自动提交推送 Runbook（强制执行）

根目录已配置全局 Git 仓库与 GitHub 远程源 `https://github.com/fure1303-max/minecamera.git`。

- **双分支定位**：
  - `refactor/multi-version`：内部多版本全景工作台（自用，用于全版本代码直观对比与历史回溯）；
  - `main`：官方正统主分支（对外开源门面，始终展示当前最新版本标准工程）。
- **日常改动自动提交推送准则**：
  - 无论功能改动、Bug 修复还是文档调整，完成后必须自动执行 `git add`、规范提交与推送；
  - 涉及 Java 逻辑时前置运行单元测试（`.\gradlew.bat test --no-parallel`）。
- **忽略防线**：
  - 严禁将 `release/*.jar` 纳入 Git 暂存（由 `.gitignore` 忽略，只通过 GitHub Releases 托管）；
  - 严禁提交 `.gradle/`、`build/`、`logs/` 或敏感环境文件。

## 5. 核心系统速查

### A. 相机控制与光学状态
- `CameraModeSession`：相机状态生命周期控制器（管理就绪、瞄准、回放状态转换）。
- `CameraOpticalState`：焦距、光圈、曝光状态。
- `CameraPoseResolver`：手持视角与三脚架机位视角矩阵结算。
- `CameraProjectionMath`：视场角（FOV）、投影变换与裁剪数学计算。

### B. 取景与渲染管线
- `CameraHudRenderer`：取景框网格线、参数角标、HUD 界面渲染。
- `MineCameraOffscreenRenderService`：利用独立 Framebuffer 进行离屏高分辨率捕获。
- `IrisCompatBridge` / `IrisHandRendererMixin`：对 Sodium / Iris 光影的渲染兼容性防护。

### C. 照片展示框与多框拼图
- `PhotoDisplayFrameBlock` & `PhotoDisplayFrameBlockEntity`：照片展示框方块与实体。
- `DisplayGroupResolver`：检测相邻照片展示框，计算连通矩形网格。
- `DisplayTextureSliceResolver`：计算大图切片 UV，使一张照片跨多个展示框完美拼接显示。

### D. 网络同步与存储
- `LocalMediaCatalog` & `MediaIndexStore`：本地已拍照片索引与元数据存储。
- `ServerDisplayMediaStore`：服务端展示框照片元数据管理。
- `MineCameraNetworking`：基于 Fabric Payload API 的双向数据传输（拍摄提交、缩略图同步、机位更新）。
