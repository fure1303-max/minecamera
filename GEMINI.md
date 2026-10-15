# MineCamera 模组开发规范与工作流规范

本项目是 Minecraft Java 1.21.11 的 Fabric 相机模组（`MineCamera`）。

---

## 1. 项目架构与多版本共存规范

- **多版本平铺共存**：
  每个版本的源码均在项目根目录下拥有独立的版本文件夹，命名格式严格为：
  `MineCamera v<version>`（例如：`MineCamera v0.1.2`、`MineCamera v0.2.0`、`MineCamera v0.2.2` 等）。
- **版本文档完备性（强制）**：
  每个版本文件夹内除源码外，必须同步维护一份 `VERSION_NOTES.md` 文档，详尽记录该版本所做的工作（干了什么）、新增或变更的功能（更新了什么）、修复的漏洞与缺陷（含根因与解决方案），以及测试验证数据。
- **开启新版本迭代流程**：
  1. 以当前最新版本的文件夹为蓝本，复制生成新版本文件夹 `MineCamera v<新版本号>`；
  2. 修改新文件夹内的 `gradle.properties`（`mod_version=<新版本号>`）；
  3. 创建并维护该目录下的 `VERSION_NOTES.md`；
  4. 在 `CHANGELOG.md` 顶部插入新版本的更新记录；
  5. 同步更新 `README.md` 中的版本链接与说明；
  6. 在新文件夹内进行代码编写、测试与编译。

---

## 2. 运行环境、源码架构与质量保障

- **基础依赖版本**：
  - Minecraft：`1.21.11`
  - Fabric Loader：`0.19.3`
  - Fabric API：`0.141.4+1.21.11`
  - Fabric Loom：`1.17.12`
  - Java 运行时：Java 21（OpenJDK 21）
- **本地 JDK 路径**：
  `C:\Users\FuRE\AppData\Roaming\.minecraft\runtime\java-runtime-delta`
- **命令行编译/测试模板**：
  在执行 Gradle 命令前，需确保 `JAVA_HOME` 指向上述 Java 21 路径：
  ```powershell
  $env:JAVA_HOME = "C:\Users\FuRE\AppData\Roaming\.minecraft\runtime\java-runtime-delta"
  $env:Path = "$env:JAVA_HOME\bin;$env:Path"
  .\gradlew.bat test --no-parallel
  .\gradlew.bat build --no-parallel
  ```
- **源码集划分（Split Source Sets）**：
  - `src/main/java`：服务端与客户端通用逻辑、物品注册、方块、方块实体、数据包网络通道。
  - `src/client/java`：客户端独占逻辑、取景器 HUD 渲染、离屏 Framebuffer 截图渲染、Iris/Sodium 着色器兼容、输入路由与 Mixin。
  - `src/main/resources`：模型、贴图、配方、语言文件与 `fabric.mod.json`。
  - `src/test/java`：纯 Java 领域逻辑的单元测试（JUnit 5）。
- **质量保障与测试准则**：
  模组采用了领域逻辑与游戏引擎解耦的设计（光学计算、胶卷服务、展示框拓扑、网络编解码等均有独立逻辑类）。在修改或新增功能后，必须运行 `.\gradlew.bat test --no-parallel` 确保已有单元测试全部通过，并为新增核心逻辑补充单元测试。

---

## 3. Git 版本控制与双分支协同架构

- **双分支架构与定位**：
  - **`refactor/multi-version` 分支（内部全景工作台 / 自用回溯）**：
    - **定位**：供开发者与 AI 协同工作使用的全景档案库。
    - **形态**：根目录下平铺共存各个版本的独立文件夹（`MineCamera v0.1.2`、`v0.2.0`、`v0.2.1`、`v0.2.2` 等）与 `VERSION_NOTES.md`，用于在本地无需切换分支即可直观对比各版本代码演进、排查历史问题与追溯细节。
  - **`main` 分支（官方正统主分支 / 对外开源门面）**：
    - **定位**：面向开源社区、外部开发者与玩家的标准发布仓库。
    - **形态**：根目录直接存放当前最新版本的单一工程源码（`src/`、`build.gradle` 等），保持标准的 Fabric 模组仓库结构，任何人克隆后均可直接编译运行。
- **文件属性与忽略防线**：
  - 根目录 `.gitattributes` 统一管控文本换行（`gradlew` 为 `LF`，批处理为 `CRLF`，图片与 jar 声明为 `binary`）。
  - 根目录 `.gitignore` 全局递归排除所有子版本文件夹内的构建临时产物（`**/.gradle/`、`**/build/`）、运行及日志（`**/run/`、`**/logs/`、`*.log`）、IDE 配置（`**/.idea/`、`**/*.iml` 等）。
  - `release/` 文件夹通过 `release/.gitkeep` 纳入版本结构管理；所有编译生成的 `release/*.jar` 成品包一律由 `.gitignore` 忽略（仅用于通过 GitHub Releases 分发，严禁提交二进制 jar 到仓库避免历史膨胀）。

---

## 4. 新版本发版、产物归档与 GitHub Releases 规范（强制）

每当完成一个新正式版本（如 `v0.2.3`、`v0.3.0` 等）的开发与测试后，必须严格遵循以下标准发版流水线：

1. **产物编译与本地归档**：
   - 在新版本子目录下执行构建，将生成的成品 jar 文件（`build/libs/minecamera-<version>.jar`）按版本号命名后复制归档至项目根目录 `release/` 文件夹（`release/minecamera-<version>.jar`）。
2. **工作台全景归档（`refactor/multi-version` 分支）**：
   - 确认版本开发笔记 `VERSION_NOTES.md`、更新日志 `CHANGELOG.md` 与根目录 `README.md` 编制完毕；
   - 提交并推送到内部工作台分支 `refactor/multi-version`，保全多版本平铺全貌。
3. **官方主分支同步（`main` 分支）**：
   - 将该新版本的干净源码（`src/`、`build.gradle` 等）与专属 `README.md` 直接提交推送到 GitHub 的 `main` 主分支，确保仓库首页始终呈现最新版本。
4. **发布 GitHub Releases 资产**：
   严格遵循已有发布标准在 GitHub 上创建 Release：
   - **Tag 格式**：`v<version>`（例如：`v0.2.2`）
   - **Release 标题**：`MineCamera v<version>`（例如：`MineCamera v0.2.2`）
   - **Release 附件（Asset）**：`minecamera-<version>.jar`（直接取自 `release/minecamera-<version>.jar` 归档）
   - **Release 内容说明**：同步提取 `CHANGELOG.md` 中对应版本的更新日志与重要修复。

---

## 5. 日常改动自动提交与推送规范（核心强制规则）

- **自动触发执行**：无论执行的是新功能开发、缺陷修复、配置微调还是文档更新，**在每轮改动工作完成后，必须自动执行 Git 暂存、提交并推送到 GitHub 远程仓库**，不需要用户额外催促或提醒。
- **前置质量检查**：若改动涉及源码或资源，提交前必须运行相关单元测试（`.\gradlew.bat test --no-parallel`），确保测试通过后再执行提交。
- **规范化提交格式**：提交信息严格遵循 Conventional Commits 规范，格式为 `<type>(<scope>): <description>`（如 `feat(camera): ...`、`fix(tripod): ...`、`docs: update GEMINI.md`）。
- **标准提交推送命令**：
  ```powershell
  git add .
  git commit -m "<type>(<scope>): <description>"
  git push
  ```
