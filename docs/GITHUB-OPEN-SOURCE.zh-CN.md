# MineCamera GitHub 开源与发版说明

这份文档不是给玩家看的，而是给仓库维护者看的。  
如果你以后要继续维护 `MineCamera`，这里就是最实用的入门说明。

## 先把几个概念说清楚

### 1. 仓库

GitHub 仓库可以理解成这个 mod 的“公开源码家”。

这里面放的是：

- 源代码
- 资源文件
- 文档
- 构建配置
- 历史提交记录

当前仓库地址：

- [https://github.com/fure1303-max/minecamera](https://github.com/fure1303-max/minecamera)

### 2. Commit

`commit` 就是一次带说明的存档点。

比如：

- 你修了一个 bug
- 你加了一个功能
- 你改了文档

都可以提交一次 commit。

### 3. Push

`push` 就是把你本地已经提交好的内容上传到 GitHub。

只在本地 commit，不 push，别人是看不到的。

### 4. Release

`Release` 不是源码本身，而是“对外发布版本页”。

通常用于放：

- 版本说明
- 对应 tag
- 玩家直接下载的 jar

当前发布页：

- [https://github.com/fure1303-max/minecamera/releases/tag/v0.1.0](https://github.com/fure1303-max/minecamera/releases/tag/v0.1.0)

### 5. Tag

`tag` 是给某个 commit 打版本标记，比如：

- `v0.1.0`
- `v0.2.0`

Release 一般就是围绕某个 tag 建的。

### 6. Issue

`Issue` 可以理解成公开问题单。

常见用途：

- 用户反馈 bug
- 记录待做功能
- 讨论某个设计问题

### 7. Actions

GitHub Actions 是 GitHub 自带的自动化。

你现在这个仓库里，最常见的用途就是：

- 每次 push 或 pull request 时自动构建
- 自动跑测试

## 你这个 MineCamera 仓库现在是什么状态

当前是一个已经能公开展示和继续维护的源码仓库，核心信息是：

- 默认分支：`main`
- 开源协议：`MIT`
- 运行基线：
  - `Minecraft 1.21.11`
  - `Fabric Loader 0.19.3`
  - `Java 21`
- 已有 GitHub Release：`v0.1.0`
- Release 附件：`minecamera-0.1.0.jar`

## 以后你最常用的操作

### 场景 1：你改了代码，想同步到 GitHub

常见流程：

```powershell
git status
git add .
git commit -m "你的提交说明"
git push origin main
```

这一步只是在“更新源码仓库”，还不等于给玩家发新版本。

### 场景 2：你想给玩家发布一个新 jar

常见流程：

1. 先改代码
2. 本地测试
3. 构建新 jar
4. 更新版本号
5. commit + push
6. 新建 tag / Release
7. 把 jar 作为附件上传到 Release

本地构建命令：

```powershell
.\gradlew.bat build --no-daemon
```

jar 默认在：

```text
build\libs\
```

## 你以后发新版本时，最少要改哪几个地方

至少检查这些：

### 1. `gradle.properties`

这里面的：

```text
mod_version=0.1.0
```

如果你要发新版本，比如 `0.2.0`，就要先改成：

```text
mod_version=0.2.0
```

### 2. `CHANGELOG.md`

把这一版新增了什么、修了什么写进去。

### 3. GitHub Release

新建一个对应版本的 Release，比如：

- `v0.2.0`

然后把新 jar 上传上去。

## 源码仓库和 jar 下载是什么关系

很多刚开始接触 GitHub 的人容易把这两件事混在一起。

你可以这样理解：

- 仓库：给开发者和查看源码的人看的
- Release：给普通玩家下载成品 jar 用的

也就是说：

- 想看代码，去仓库首页
- 想下载成品，去 Releases

## 什么时候需要我帮你做什么

以后如果你说这些话，我会直接按对应方向帮你处理：

- “帮我发一个新版本到 GitHub”
- “把这次改动做成 release”
- “把版本号升到 0.2.0”
- “帮我整理一下 README / CHANGELOG”
- “帮我看看这次该不该开 issue”

## 一句话总结

你现在已经有了一个能公开访问的 MineCamera 源码仓库；  
以后你日常维护基本就是：`改代码 -> 测试 -> commit -> push`，如果要给玩家发新成品，再额外做 `Release + 上传 jar`。
