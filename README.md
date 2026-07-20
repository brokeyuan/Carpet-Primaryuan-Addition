# Carpet PRY Addition

> **Carpet Mod 扩展：假人增强工具集** — 为 Carpet 假人系统提供名称自定义、皮肤设置、白天睡觉、珍珠传送、实体骑乘等功能。

| | |
|---|---|
| **Mod ID** | `carpet-pry-addition` |
| **版本** | 1.1.2-SNAPSHOT |
| **作者** | Brokeyuan |
| **源码** | [GitHub](https://github.com/Brokeyuan/carpet-pry-addition) |
| **许可证** | [LGPL-3.0](LICENSE) |

---

## 依赖

| 名称 | 类型 | 备注 |
|------|------|------|
| Carpet | 必须 | [MC百科](https://www.mcmod.cn/class/2361.html) |
| Fabric API | 必须 | [MC百科](https://www.mcmod.cn/class/3124.html) |
| skinrestorer | 可选 | [Modrinth](https://modrinth.com/mod/skinrestorer)（仅皮肤功能需要） |

## 版本支持

本 Mod 使用 [FallenBreath/preprocessor](https://github.com/Fallen-Breath/preprocessor) 多版本构建系统，同时支持 **9 个 MC 版本**：

```
1.21 → 1.21.1 → 1.21.3 → 1.21.4 → 1.21.5 → 1.21.8 → 1.21.10 → 1.21.11 → 26.1.2
```

以 mainProject（1.21.11）为基准，通过 Preprocessor 条件编译 + 版本源码覆盖实现跨版本兼容。

## 文档

- [规则](docs/rules.md)
- [命令](docs/commands.md)

---

## 功能特性

### 1. 假人名称自定义

覆盖 `/player` 命令的 Tab 补全建议列表，允许管理员预设常用假人名，方便快速生成。

**规则**: `fakePlayerNameSuggestions`

```bash
# 设置自定义名称列表
/carpet fakePlayerNameSuggestions Steve,Alex,Brokeyuan,Firework
```

**技术实现**: 通过 Mixin 注入 `carpet.commands.PlayerCommand.getPlayerSuggestions()` 方法，使用 `@Inject` + `cancellable = true` 完全替换原返回值，将规则配置的名称列表与在线玩家名合并为 `LinkedHashSet`。

### 2. 假人皮肤系统

在生成 Carpet 假人时自动通过 Mojang API 获取指定玩家的皮肤并应用到假人上。

**规则**: `fakePlayerSkinMode` / `fakePlayerSkinSet`

| 模式 | 行为 |
|------|------|
| `default` | 不修改皮肤，使用默认皮肤 |
| `summon` | 假人皮肤跟随执行命令的召唤者 |
| `same_skin` | 假人皮肤与同名的真实玩家一致 |

**技术实现**: 通过 Mixin 在 `PlayerCommand.spawn()` 方法尾部注入，使用 `SkinService.setSkinAsync()` 调用 skinrestorer API 异步设置皮肤。skinrestorer 为可选依赖，未安装时皮肤功能静默失败。

### 3. 白天睡觉

允许玩家在白天（非雷雨天）上床睡觉，醒来后时间自动切换到夜晚。

**规则**: `sleepingDuringTheDay`

**行为细节**:
1. **上床**: 绕过原版 `BedRule.canSleep()` 的白天检查
2. **睡眠中**: 如果被非自然方式唤醒，阻止唤醒，保持睡觉状态
3. **自然醒来** (sleepTimer >= 100): 将服务器时间设置为傍晚 (13000 tick)

**技术实现**: 采用双 Mixin 协作架构：
- `MixinPlayer` (`ServerPlayer`): 通过 `@Redirect` 绕过 `BedRule.canSleep()` 白天限制
- `MixinPlayerBase` (`Player`): 通过 `@WrapOperation` 拦截 `stopSleepInBed()`，控制唤醒时机和时间修正

### 4. 假人珍珠传送

基于 Carpet 假人的珍珠传送系统。玩家通过"站点"快速传送到假人位置。

**规则**: `TppFakePlayer`

**命令**:
- `/tpp <station>` — 传送到指定站点
- `/tppset` — 站点管理命令

**技术实现**:
- **命令注册**: 使用 `CommandRegistrationCallback.EVENT` + Fabric API v2
- **异步操作**: 使用独立 `Thread` 处理假人生命周期，避免阻塞主线程
- **线程安全**: 使用 `CountDownLatch` 确保 `server.execute()` 回调完成后再继续轮询
- **权限校验**: 通过读取 `ops.json` 判断管理员身份

### 5. 骑乘玩家

允许玩家骑乘其他玩家。主手持不死图腾时右键其他玩家即可骑上对方。

**规则**: `ridingPlayers`

**技术实现**: 通过 `UseEntityCallback` 事件拦截玩家与实体的交互，根据规则状态决定骑乘行为。支持潜行抖落乘客、登出自动下车、游戏模式变更下车等特性。

### 6. 捡起玩家

允许玩家将其他玩家"捡"到自己头上。主手持不死图腾+副手金胡萝卜时右键其他玩家即可捡起。

**规则**: `pickupPlayers`

**技术实现**: 通过 `UseEntityCallback` 事件拦截玩家与实体的交互，根据规则状态决定拾取行为。与骑乘玩家规则独立，可单独启用/禁用。

### 7. Unicode 参数支持

允许命令参数中使用 Unicode 字符（如中文站点名）。

**规则**: `unicodeArgumentsSupport`

**技术实现**: 通过 Mixin 修改 `StringReader` 的字符解析逻辑，支持 UTF-8 字符。

---

## 安装与使用

### 依赖要求

| 依赖 | 版本要求 | 必选 |
|------|----------|------|
| Fabric Loader | ≥ 0.16.0 | 是 |
| Fabric Carpet | ≥ 1.4.x | 是 |
| skinrestorer | ≥ 2.4.1 | 否 |

### 快速开始

1. 安装 Fabric Loader + Carpet Mod 到服务端
2. (可选) 安装 skinrestorer 以启用假人皮肤功能
3. 将 `carpet-pry-addition-v1.1.2-mc<版本>.jar` 放入 `mods/` 目录
4. 启动服务器，使用 `/carpet` 命令管理规则：

```bash
# 查看所有 PRY 规则
/carpet pry

# 开启常用功能
/carpet fakePlayerNameSuggestions Steve,Alex,MyFake1,MyFake2
/carpet fakePlayerSkinMode summon
/carpet sleepingDuringTheDay true
/carpet TppFakePlayer true
/carpet entitiesRidingPlayers true
```

---

## 开发者文档

### 构建系统

```
构建工具: Gradle 9.4.1
Loom 版本: Fabric Loom 1.15.3 (双插件策略)
Preprocessor: com.replaymod.preprocess c5abb4fb12
Java: 21+
```

#### 双 Loom 插件策略

| 插件 | 适用版本 | Mapping |
|------|----------|---------|
| `fabric-loom-remap` | 1.21 ~ 1.21.11 | intermediary（有混淆） |
| `fabric-loom` | 26.1.2 | unobfuscated（无混淆） |

#### 版本链结构

```groovy
mc121 → mc1211 → mc1213 → mc1214 → mc1215 → mc1218 → mc12110 → mc12111 → mc260102
```

#### 版本覆盖机制

对于无法通过 Preprocessor `#if`/`#else` 处理的差异，使用版本特定源码覆盖：

```
src/main/java/.../SomeClass.java          ← 基础代码（mainProject = 1.21.11）
versions/1.21/src/main/java/.../SomeClass.java  ← 1.21 版本覆盖
versions/1.21.1/src/main/java/.../SomeClass.java ← 1.21.1 版本覆盖
```

### Mixin 架构

#### Mixin 注册表

| Mixin 类 | 目标类 | 注入方式 | 职责 |
|----------|--------|----------|------|
| `PlayerCommandMixin` | `carpet.commands.PlayerCommand` | `@Inject` | 假人名称建议 |
| `PlayerCommandSkinMixin` | `carpet.commands.PlayerCommand` | `@Inject` | 假人皮肤设置 |
| `MixinPlayer` | `ServerPlayer` | `@Redirect` | 白天睡觉（上床阶段） |
| `MixinPlayerBase` | `Player` | `@WrapOperation` | 白天睡觉（醒来阶段） |
| `MixinServerLevel` | `ServerLevel` | `@Inject` | 白天睡觉（时间修正） |
| `StringReaderUnicodeSupportMixin` | `StringReader` | `@Inject` | Unicode 参数支持 |
| `EntityMixin` | `Entity` | `@Inject` | 实体骑乘玩家 |
| `PlayerMixin` | `Player` | `@Inject` | 实体骑乘玩家 |
| `ProjectileUtilMixin` | `ProjectileUtil` | `@Inject` | 实体骑乘玩家 |
| `RideCommandMixin` | `RideCommand` | `@Inject` | 实体骑乘玩家（/ride 扩展） |
| `ServerPlayerMixin` | `ServerPlayer` | `@Inject` | 实体骑乘玩家（登出处理） |

#### 版本兼容策略

| API 差异 | 影响版本 | 解决方案 |
|----------|----------|----------|
| `GameProfile.name()` vs `.getName()` | 1.21 ~ 1.21.8 vs 1.21.10+ | Preprocessor 条件编译 |
| `BedRule` 类不存在 | 1.21 ~ 1.21.8 | 版本覆盖：空壳 MixinPlayer |
| `getPlayer(String)` 返回类型 | UUID vs ServerPlayer | 版本覆盖 |
| `SkinService.setSkinAsync` 参数 | `Collection<GameProfile>` vs `Collection<ServerPlayer>` | 版本覆盖 |
| Mixin compatibility level | JAVA_17 ~ JAVA_25 | Gradle 资源过滤 |

### 项目结构

```
carpet-primaryuan/
├── build.gradle                 # 主构建脚本（Preprocessor 版本链）
├── settings.gradle              # 动态包含所有版本子项目
├── settings.json                # 版本列表
├── common.gradle                # 共享构建逻辑
├── gradle.properties            # 项目元数据
├── libs/                        # 本地依赖 JAR
│   ├── fabric-carpet-*.jar
│   ├── fabric-loader-*.jar
│   └── skinrestorer-*.jar
├── src/main/
│   ├── java/me/primaryuan/carpet/
│   │   ├── CarpetPrimaryuanMod.java       # Mod 入口
│   │   ├── CarpetPrimaryuanServer.java    # CarpetExtension 实现
│   │   ├── CarpetPrimaryuanSettings.java  # 规则定义
│   │   ├── TppConfigManager.java          # TPP 配置管理
│   │   ├── command/
│   │   │   └── TppCommand.java            # /tpp + /tppset 命令
│   │   ├── handler/ridingPlayers/
│   │   │   └── EntitiesRidingPlayersHandler.java
│   │   ├── mixins/rule/                   # Mixin 实现
│   │   └── settings/
│   │       └── CarpetRuleRegistrar.java   # 规则注册辅助
│   └── resources/
│       ├── fabric.mod.json                # Mod 元数据
│       ├── carpet-primaryuan.mixins.json  # Mixin 配置
│       ├── carpet-primaryuan.accesswidener
│       └── assets/carpetprimaryuan/lang/  # 语言包
├── versions/                    # 版本特定源码覆盖
│   ├── 1.21/
│   ├── 1.21.1/
│   ├── ...
│   ├── 1.21.11/                # mainProject
│   └── 26.1.2/
├── docs/                        # 文档
│   ├── rules.md
│   └── commands.md
└── README.md
```

---

## 构建

```bash
# 构建全部 9 个版本
./gradlew build

# 清理后重建
./gradlew clean build

# 收集所有 JAR 到 build/libs/
./gradlew buildAndGather
```

输出 JAR 命名格式: `carpet-pry-addition-v{mod_version}-mc{mc_version}-SNAPSHOT.jar`

---

## 许可证

本项目基于 [LGPL-3.0](LICENSE) 许可证开源。