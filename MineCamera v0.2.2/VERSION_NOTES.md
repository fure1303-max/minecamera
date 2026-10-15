# MineCamera v0.2.2 版本开发与功能记录

## 1. 版本基本信息

- **版本号**：`v0.2.2`
- **对应构建产物**：`release/minecamera-0.2.2.jar`
- **支持环境**：`Minecraft Java 1.21.11` + `Fabric Loader 0.19.3` + `Java 21`
- **版本定位**：新增 3:2 4K 极限画质预设、照片展示框交互体验全面重构（空手右键取下照片、贴胶卷消耗与旧照片防吞换回、展示框拆除防丢掉落机制）。

---

## 2. 具体工作与代码修改（干了什么、更新了什么、修复了什么）

### 2.1 新增 3:2 4K 摄影分辨率预设
- **修改文件**：
  - `src/main/java/com/minecamera/logic/CameraModePresetSet.java`
  - `src/test/java/com/minecamera/logic/ResolutionValidatorTest.java`
- **改动说明**：
  在相机预设集合 `CameraModePresetSet.PRESETS` 中增加 `ResolutionPresets.maxPreset("3:2 4K", 3, 2)`。该预设输出分辨率为 `3527 x 2351`（总像素 8,291,977，严格符合不超过 8,294,400 像素的软上限），完美满足摄影爱好者对 3:2 全画幅比例的 4K 极致画质需求。

### 2.2 展示框空手右键取下照片与归还胶卷机制
- **修改文件**：
  - `src/main/java/com/minecamera/world/block/PhotoDisplayFrameBlock.java`
  - `src/main/java/com/minecamera/logic/DisplayFrameStateLogic.java`
  - `src/main/java/com/minecamera/world/block/entity/PhotoDisplayFrameBlockEntity.java`
  - `src/main/java/com/minecamera/film/FilmService.java`
  - `src/test/java/com/minecamera/logic/DisplayFrameStateLogicTest.java`
- **改动说明**：
  1. 在 `DisplayFrameStateLogic` 中新增 `removePhoto` 拓扑算法：点击任意属于某个相片组的展示框网格时，精准定位并清除该相片对应的整个矩形区域内所有展示框的渲染状态，且不影响墙面上并存的其他独立相片区域。
  2. 在 `PhotoDisplayFrameBlock.onUse` 中实现交互检测：当玩家主手空手右键有照片的展示框时，触发取相片逻辑：
     - 清除该相片关联展示框组的所有展示状态；
     - 读取该相框保存的 `FilmRecord` 完整元数据，并通过 `FilmService.createExposedFilmStack` 重新生成为已曝光胶卷物品；
     - 优先放入玩家背包（`player.getInventory().insertStack`），背包已满时掉落于脚边（`dropStack`）；
     - 播放原版展示框物品取下音效（`SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM`）；
     - 返回 `ActionResult.SUCCESS`，触发玩家手部挥动。

### 2.3 贴上胶卷消耗与旧照片替换回收机制
- **修改文件**：
  - `src/main/java/com/minecamera/world/block/PhotoDisplayFrameBlock.java`
  - `src/main/java/com/minecamera/film/FilmService.java`
- **改动说明**：
  1. **胶卷消耗**：玩家手持已曝光胶卷右键展示框贴相片时，若玩家处于生存模式（`!player.isCreative()`），正常扣减手持堆叠的 1 卷胶卷（`stack.decrement(1)`）；创造模式下不扣减。
  2. **旧相片回收（防吞照片机制）**：若目标展示框当前已有旧照片展示，在贴入新照片的同时，系统会自动将原本展示的旧相片提取为胶卷物品，原样返还给玩家背包（背包满则在现场掉落），彻底解决用新相片覆盖旧相片导致玩家旧照片胶卷丢失的漏洞。
  3. 播放原版展示框物品放入音效（`SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM`）。

### 2.4 展示框拆除防丢掉落机制
- **修改文件**：
  - `src/main/java/com/minecamera/world/block/PhotoDisplayFrameBlock.java`
- **改动说明**：
  在 `PhotoDisplayFrameBlock.onStateReplaced` 中，方块被敲掉前预先读取其展示相片的 `FilmRecord`。方块被破坏后执行相邻相框重分区（`repartitionNeighboringFrames`）。若重组后相邻展示框未能继续承载该相片（例如单框相框被破坏，或拼接展示框被破坏导致相片被解散），则在现场自动掉落该相片的已曝光胶卷物品，杜绝任何误操作或破坏方块导致相片永久蒸发的风险。

### 2.5 胶卷完整元数据（FilmRecord）持久化存储
- **修改文件**：
  - `src/main/java/com/minecamera/world/block/entity/PhotoDisplayFrameBlockEntity.java`
  - `src/main/java/com/minecamera/film/FilmService.java`
- **改动说明**：
  1. 为 `FilmService` 增加 `ReadView` / `WriteView` 存储编解码方法（`readFromView`、`writeToView`），并在 `PhotoDisplayFrameBlockEntity` 的 NBT 数据中持久化保存 `FilmRecord`。
  2. 当展示框拼图因增减相框发生重组扩张或收缩时，`repartitionComponent` 会在内部映射表（`filmByMedia`）中保全各个相片的 `FilmRecord`，确保展示框无论经过多少次重组、世界重启或区块卸载，在被空手取下或敲掉时，都能百分之百完整还原拍摄作者、拍摄时间、焦距、光圈等元数据。

---

## 3. 测试与验证

- **单元测试执行情况**：
  运行命令：`.\gradlew.bat test --no-parallel`
  执行结果：**共 171 项单元测试全部通过（100% 通过率）**。
  - `DisplayFrameStateLogicTest`：新增整张多格相片取下、周围相片保持独立、空相框点击边界测试；
  - `ResolutionValidatorTest`：新增 3:2 4K 预设尺寸与比例合法性校验。
- **构建打包情况**：
  运行命令：`.\gradlew.bat build --no-parallel`
  构建产物：`MineCamera v0.2.2/build/libs/minecamera-0.2.2.jar`（252,897 字节）。
- **产物归档**：
  已按规范归档至根目录：`release/minecamera-0.2.2.jar`。
