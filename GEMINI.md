# MineCamera 模组开发规范与工作流规范

本项目是 Minecraft Java 1.21.11 的 Fabric 相机模组（`MineCamera`）。

## 1. 项目多版本文件夹规范（强制）

- **多版本共存**：每个版本的源码均在项目根目录拥有独立的版本文件夹，命名格式严格为：
  `MineCamera v<version>`（例如：`MineCamera v0.1.2`、`MineCamera v0.1.3` 等）。
- **迭代新版本流程**：
  1. 当需要开启新版本开发时，以当前最新版本的文件夹为蓝本，在根目录下复制创建新的版本文件夹 `MineCamera v<新版本号>`。
  2. 修改新版本文件夹内的 `gradle.properties`：
     ```properties
     mod_version=<新版本号>
     ```
  3. 在 `CHANGELOG.md` 中增加对应版本的改动记录。
  4. 在 `README.md` 中同步更新版本说明与链接（如适用）。
  5. 在新版本文件夹内进行功能开发、测试与构建。

## 2. 运行环境与 Java 配置

- **Minecraft 版本**：`1.21.11`
- **Fabric Loader**：`0.19.3`
- **Fabric API**：`0.141.4+1.21.11`
- **Fabric Loom**：`1.17.12`
- **Java 运行时**：Java 21（OpenJDK 21）
- **本地 JDK 路径**：
  `C:\Users\FuRE\AppData\Roaming\.minecraft\runtime\java-runtime-delta`
- **执行构建/测试注意事项**：
  在命令行执行 Gradle 命令前，需确保 `JAVA_HOME` 指向上述 Java 21 路径：
  ```powershell
  $env:JAVA_HOME = "C:\Users\FuRE\AppData\Roaming\.minecraft\runtime\java-runtime-delta"
  $env:Path = "$env:JAVA_HOME\bin;$env:Path"
  .\gradlew.bat test
  .\gradlew.bat build
  ```

## 3. release 产物归档规范（强制）

- **统一产物归档**：在项目根目录下维护 `release/` 文件夹（`d:\Gemini\MineCamera\release`）。
- **Jar 归档要求**：
  从今往后，任何版本执行构建成功后，必须将生成的成品 jar 文件（如 `build/libs/minecamera-<version>.jar`）按版本号命名（`minecamera-<version>.jar`）复制归档放入根目录的 `release/` 文件夹中。
- **发布产物来源**：后续发布到 GitHub Releases 或分发给用户时，直接使用 `release/` 目录中的归档 jar。

## 4. GitHub Releases 发布格式规范（强制）

发布新版本到 GitHub Releases 时，必须严格遵循既有格式：
- **Tag 格式**：`v<version>`（例如：`v0.1.2`）
- **Release 标题**：`MineCamera v<version>`（例如：`MineCamera v0.1.2`）
- **Release 附件（Asset）**：`minecamera-<version>.jar`（直接取自 `release/minecamera-<version>.jar`）
- **Release 说明**：同步提取 `CHANGELOG.md` 中对应版本的更新日志

## 5. 模组架构与开发准则

- **源码集划分（Split Source Sets）**：
  - `src/main/java`：服务端与客户端通用逻辑、物品注册、方块、方块实体、数据包网络通道。
  - `src/client/java`：客户端独占逻辑、取景器 HUD 渲染、离屏 Framebuffer 截图渲染、Iris/Sodium 着色器兼容、输入路由与 Mixin。
  - `src/main/resources`：模型、贴图、配方、语言文件与 `fabric.mod.json`。
  - `src/test/java`：纯 Java 领域逻辑的单元测试（JUnit 5）。
- **质量保障**：
  模组采用了领域逻辑与游戏引擎解耦的设计（如光学计算、胶卷服务、展示框拓扑、网络编解码等均有独立逻辑类）。在修改或新增功能后，必须运行 `./gradlew.bat test` 确保已有单元测试全部通过，并为新增核心逻辑补充单元测试。

## 6. 版本开发文档规范（强制）

- **版本文档完备性**：每个版本文件夹内（`MineCamera v<version>/`）除源码外，必须同步维护一份 `VERSION_NOTES.md` 文档。
- **记录内容**：详细记录包括但不限于该版本所做的工作（干了什么）、新增或变更的功能（更新了什么）、修复的漏洞与缺陷（修复了什么，附带问题根因和修改方案），以及测试验证情况。
- **目的**：确保无论上下文跨度多长，每个版本的开发过程、变更细节与遗留问题都能精准追溯。

## 7. Git 版本控制、双分支协同与自动推送规范（强制）

- **双分支架构与定位**：
  - **`refactor/multi-version` 分支（内部全景工作台 / 自用回溯）**：
    - **定位**：供开发者与 AI 协同工作使用的全景档案库。
    - **形态**：根目录下平铺共存各个版本的独立文件夹（`MineCamera v0.1.2`、`v0.2.0`、`v0.2.1`、`v0.2.2` 等）与 `VERSION_NOTES.md`。用于在本地无需切换分支即可直观对比各版本代码演进、排查历史问题与追溯细节。
  - **`main` 分支（官方正统主分支 / 对外开源门面）**：
    - **定位**：面向开源社区、外部开发者与玩家的标准发布仓库。
    - **形态**：根目录直接存放当前最新版本的单一工程源码（`src/`、`build.gradle` 等），保持标准的 Fabric 模组仓库结构，任何人克隆后均可直接编译运行。
- **每产出新版本直接上传主分支规范（强制执行）**：
  - **以后每当产出一个新的正式版本**（如 `v0.2.3`、`v0.3.0` 等），必须执行以下发版同步流水线：
    1. **工作台归档**：在当前工作区完成新版本代码与 `VERSION_NOTES.md` 编制，运行单元测试通过后，归档 jar 到 `release/`；
    2. **同步主分支**：将该新版本的干净源码与专属 `README.md` 直接提交推送到 GitHub 的 `main` 主分支，确保仓库首页始终呈现最新版本；
    3. **创建版本 Tag**：在 `main` 分支对应提交上创建 `v<version>` 标签并推送到 GitHub；
    4. **发布 Release 资产**：在 GitHub Releases 中创建新 Release，上传对应的 `minecamera-<version>.jar` 成品并附带更新日志。
- **忽略过滤与产物归档**：
  - 根目录 `.gitignore` 全局递归排除所有子版本文件夹内的构建临时产物（`**/.gradle/`、`**/build/`）、运行及日志（`**/run/`、`**/logs/`、`*.log`）、IDE 配置（`**/.idea/`、`**/*.iml` 等）。
  - `release/` 文件夹通过 `release/.gitkeep` 纳入版本结构管理；所有编译生成的 `release/*.jar` 成品包一律由 `.gitignore` 忽略（成品包仅作为本地发布缓存并通过 GitHub Releases 分发，严禁提交二进制 jar 到仓库避免历史膨胀）。
- **日常改动自动提交与推送（核心强制规则）**：
  - **自动触发执行**：无论执行的是新功能开发、缺陷修复、配置微调还是文档更新，**在每轮改动工作完成后，必须自动执行 Git 暂存、提交并推送到 GitHub 远程仓库**，不需要用户额外催促或提醒。
  - **前置质量检查**：若改动涉及源码或资源，提交前必须运行相关单元测试（`.\gradlew.bat test --no-parallel`），确保测试通过后再执行提交。
  - **规范化提交格式**：提交信息严格遵循 Conventional Commits 规范，格式为 `<type>(<scope>): <description>`（如 `feat(camera): ...`、`fix(tripod): ...`、`docs: update GEMINI.md`）。
  - **标准提交推送命令**：
    ```powershell
    git add .
    git commit -m "<type>(<scope>): <description>"
    git push
    ```

