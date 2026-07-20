# 命令

## 假人珍珠传送 (`/tpp`)

### 语法

`/tpp <station>`

- 参数 `<station>`：目标传送站点名称（支持内部名或显示名）

### 效果

传送到指定站点（通过假人中转）。需要启用 `TppFakePlayer` 规则。

### 工作流程

1. 构建假人名：别名（如有）或玩家名（截断至 10 字符） + `_` + 站点名
2. 执行 `/player <假人名> rejoin`（让已有假人重新加入）
3. 轮询等待假人上线（最多 10 秒）
4. 执行 `/player <假人名> use`（传送者操控假人右键）
5. 等待传送完成
6. 执行 `/player <假人名> kill`（清除假人）

### 示例

```bash
# 传送到名为 spawn 的站点
/tpp spawn

# 传送到名为 base 的站点
/tpp base
```

---

## 站点管理 (`/tppset`)

### 语法

```
/tppset
    ├── spawn <station>          # 设置假人生成点
    ├── set <name> [<displayName>]  # 添加站点
    ├── remove <station>         # 删除站点
    ├── rename <player>
    │       ├── set <alias>      # 设置玩家别名
    │       └── remove           # 移除玩家别名
    └── rule
            └── use <count>      # 设置右键次数
```

### 子命令详解

#### `/tppset spawn <station>`

在当前位置设置该站点的假人生成点，立即生成假人并在 3 秒后自动下线。

- 参数 `<station>`：站点名称
- 权限：需要启用 `TppFakePlayer` 规则

#### `/tppset set <name> [<displayName>]`

添加传送站点。

- 参数 `<name>`：站点内部名称（用于命令输入）
- 参数 `<displayName>`：可选，站点显示名称（用于提示信息）
- 权限：管理员专属

#### `/tppset remove <station>`

删除传送站点。

- 参数 `<station>`：站点名称（支持内部名或显示名）
- 权限：管理员专属

#### `/tppset rename <player> set <alias>`

为玩家设置假人传送别名，用于构建更短的假人名。

- 参数 `<player>`：玩家真实名称
- 参数 `<alias>`：别名（最多 12 字符）
- 权限：管理员专属

#### `/tppset rename <player> remove`

移除玩家的假人传送别名。

- 参数 `<player>`：玩家真实名称
- 权限：管理员专属

#### `/tppset rule use <count>`

设置传送时假人右键末影珍珠的次数。

- 参数 `<count>`：右键次数（1-5）
- 权限：管理员专属

#### `/tppset rule`

查看当前 TPP 规则配置。

- 权限：管理员专属

### 别名系统

管理员可为玩家设置短别名，用于构建更短的假人名：

```
原始: VeryLongPlayerName_station (可能超过字符限制)
别名: VIP
结果: VIP_station (更短且安全)
```

### 示例

```bash
# 添加站点（无显示名）
/tppset set spawn

# 添加站点（带显示名）
/tppset set farm 农场

# 删除站点
/tppset remove spawn

# 为玩家设置别名
/tppset rename VeryLongPlayerName set VIP

# 移除玩家别名
/tppset rename VeryLongPlayerName remove

# 设置右键次数为 2
/tppset rule use 2

# 查看规则配置
/tppset rule
```