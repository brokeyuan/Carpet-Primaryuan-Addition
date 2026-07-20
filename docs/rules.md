# 规则

**提示：可以使用`Ctrl+F`快速查找自己想要的规则**

## 假人珍珠传送 (TppFakePlayer)

使用假人快速使用珍珠传送站。当为true时启用/tppset设置指令和/tpp 玩家指令。

- 类型: `boolean`
- 默认值: `false`
- 参考选项: `false`, `true`
- 分类: `PRIMARYUAN`, `CREATIVE`, `SURVIVAL`

## 假人名称建议 (fakePlayerNameSuggestions)

自定义/player建议的假人列表。使用','分隔每个名称。

- 类型: `string`
- 默认值: `Steve,Alex`
- 参考选项: `Steve,Alex`, `Pry,hsds`, `Pry,hsds,Firework,Food`, ``
- 分类: `PRIMARYUAN`, `CREATIVE`, `SURVIVAL`

## 假人皮肤设置 (fakePlayerSkinMode)

设置假人的皮肤。依赖 **skinrestorer** Mod。

- 类型: `string`
- 默认值: `default`
- 参考选项: `default`, `summon`, `same_skin`
- 分类: `PRIMARYUAN`, `CREATIVE`, `SURVIVAL`

### 模式说明

| 模式 | 行为 |
|------|------|
| `default` | 不更改皮肤 |
| `summon` | 使用召唤者的皮肤 |
| `same_skin` | 使用统一皮肤 |

## 假人统一皮肤设置 (fakePlayerSkinSet)

当 `fakePlayerSkinMode` 为 `same_skin` 时，设置用于假人皮肤的玩家名称。

- 类型: `string`
- 默认值: `Brokeyuan`
- 参考选项: `Brokeyuan`, `hsds`, ``
- 分类: `PRIMARYUAN`, `CREATIVE`, `SURVIVAL`

## 假人数据XaeroLib修复 (xaerolibFix)

修复高版本Xaero 搭配LuckPerms 会导致假人数据丢失的问题。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `FEATURE`

## 隐身草 (invisibleInTallGrass)

玩家头部位于高草丛时自动隐形。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`

## 玩家帽子 (playerhat)

允许玩家将物品戴在头上，并添加/hat指令。当 playerhat 启用时，放在头部槽位的不死图腾可在玩家受到致命伤害时触发死亡保护效果（再生 II、伤害吸收 II、抗火 I）。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`

## 骑乘玩家 (ridingPlayers)

主手持不死图腾时，可以骑上其他玩家。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`

## 捡起玩家 (pickupPlayers)

主手持不死图腾+副手金胡萝卜时，可以捡起其他玩家（让对方骑到自己身上）。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`

## 玩家骑乘堆叠上限 (ridingPlayersPickUpLimit)

骑乘和捡起时最多可堆叠的玩家数量，骑乘和捡起共用此上限。

- 类型: `int`
- 默认值: `16`
- 参考选项: `16`, `32`, ``
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`

## 玩家骑乘更改模式下车 (ridingPlayersDismountOnGameModeChange)

当玩家游戏模式变更的时候，让头上的玩家下车。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`

## 玩家骑乘时可交互（客户端） (ridingPlayersClientAllowInteractions)

需客户端安装，当头上有乘客的时候，仍可与方块/实体交互。

- 类型: `boolean`
- 默认值: `true`
- 分类: `PRIMARYUAN`, `SURVIVAL`, `FEATURE`, `CLIENT`

## 白日做梦 (sleepingDuringTheDay)

允许玩家在白天睡觉，睡觉后切换至夜晚（参考 PCA）。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `CREATIVE`, `SURVIVAL`

## Unicode 参数支持 (unicodeArgumentsSupport)

允许命令参数中使用非ASCII字符（中文，日文，韩文等，可以用于召唤中文名假人）（移植来自YACA）。

- 类型: `boolean`
- 默认值: `false`
- 分类: `PRIMARYUAN`, `FEATURE`