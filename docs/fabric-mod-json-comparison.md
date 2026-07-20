# fabric.mod.json 对比分析

## 概述

本文件对比当前项目 `carpet-primaryuan` 与参考项目 `Carpet-Igny-Addition` 的 `fabric.mod.json` 配置差异，帮助识别需要修复和优化的问题。

---

## 完整字段对比

### 1. 基础信息

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `schemaVersion` | `1` | `1` | ✅ 一致 | - |
| `id` | `"carpet-pry-addition"` | `"${id}"` | ✅ 均可 | - |
| `version` | `"${version}"` | `"${version}"` | ✅ 一致 | - |
| `name` | `"Carpet PRY Addition"` | `"${name}"` | ✅ 均可 | - |

### 2. 描述信息

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `description` | `"Primaryuan Server's Carpet Addition"` | `"IGNY Server's Carpet Addition"` | ✅ 已优化 | - |

### 3. 作者与联系信息

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `authors[0]` | `"Brokeyuan"` | `"Liuyue_awa"` | ✅ 正常 | - |
| `contact.homepage` | `"https://primaryuan.top:2026/"` | ❌ 无 | ❌ 需修复 | **移除个人域名**，或替换为 GitHub 页面 |
| `contact.sources` | `"https://github.com/brokeyuan/Carpet-Primaryuan-Addition"` | `"https://github.com/liuyuexiaoyu1/Carpet-Igny-Addition"` | ✅ 正确 | - |

### 4. 资源配置

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `license` | `"LGPL-3.0"` | `"LGPLv3"` | ✅ 等效 | - |
| `icon` | `"icon.png"` | `"assets/carpet-igny-addition/icon.png"` | ✅ 已确认 | 文件存在于正确位置 |

### 5. 运行环境

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `environment` | `"*"` | `"*"` | ✅ 一致 | - |

### 6. 入口点

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `entrypoints.main` | `["me.primaryuan.carpet.CarpetPrimaryuanMod"]` | `["com.liuyue.igny.IGNYServerMod"]` | ✅ 正常 | - |
| `entrypoints.client` | ❌ 无 | `["com.liuyue.igny.client.IGNYClient"]` | ✅ 不需要 | 骑乘逻辑为服务端处理，无需客户端入口 |

### 7. Mixins 与 Access Widener

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `mixins[0]` | `"carpet-primaryuan.mixins.json"` | `"carpet-igny-addition.mixins.json"` | ✅ 正常 | - |
| `accessWidener` | `"carpet-primaryuan.accesswidener"` | `"carpet-igny-addition.accesswidener"` | ✅ 正常 | - |

### 8. 依赖关系

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `depends.fabricloader` | `">=0.16.0"` | `">=0.11.4"` | ✅ 当前更严格 | - |
| `depends.minecraft` | `"${minecraft_dependency}"` | `"${minecraft_dependency}"` | ✅ 一致 | - |
| `depends.carpet` | `"${carpet_dependency}"` | `"*"` | ✅ 当前更精确 | - |
| `depends.fabric-api` | ❌ 无 | `"*"` | ❌ 需修复 | **必须添加** |
| `depends.skinrestorer` | `"${skinrestorer_dependency}"` | ❌ 无 | ⚠️ 需调整 | 应改为 `suggests`（建议）而非 `depends`（必须） |

### 9. 推荐依赖

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `recommends.xaerolib` | `"*"` | ❌ 无 | ✅ 可保留 | - |

### 10. 自定义配置

| 字段 | 当前项目 | 参考项目 | 状态 | 修改要求 |
|------|----------|----------|------|----------|
| `custom.modmenu.parent` | `"carpet"` | ❌ 无 | ✅ 正确 | - |
| `custom.modmenu.update_checker` | `true` | `true` | ✅ 已启用 | - |
| `custom.modmenu.links` | `{"modmenu.discord": "https://qm.qq.com/q/Ez582Z5P0c"}` | `{"modmenu.QQGroup": "..."}` | ✅ 已添加 | QQ群链接：PRY-原初MC交流群 |
| `custom.lithium:options` | ❌ 无 | 有配置 | ⚠️ 可选 | 优化 Lithium 兼容性 |

---

## 🔴 必须修复的问题

| # | 问题 | 位置 | 严重程度 |
|---|------|------|----------|
| 1 | **个人域名暴露** | `contact.homepage` | 🔴 高 |
| 2 | **缺少 fabric-api 依赖** | `depends` | 🔴 高 |
| 3 | **skinrestorer 应为可选依赖** | `depends.skinrestorer` | 🟡 中 |

---

## 🟡 建议优化（已完成）

| # | 优化项 | 描述 | 状态 |
|---|--------|------|------|
| 1 | **优化描述** | 修改为 "Primaryuan Server's Carpet Addition" | ✅ 已完成 |
| 2 | **添加 update_checker** | 启用 Mod Menu 更新检查 | ✅ 已完成 |
| 3 | **图标路径检查** | 确认 `icon.png` 文件存在 | ✅ 已完成 |
| 4 | **添加 QQ群链接** | 添加 custom.modmenu.links | ✅ 已完成 |

---

## 修改后的 fabric.mod.json 示例

以下是基于对比分析的优化建议版本：

```json
{
  "schemaVersion": 1,
  "id": "carpet-pry-addition",
  "version": "${version}",

  "name": "Carpet PRY Addition",
  "description": "Carpet Mod extension providing fake player enhancements including name customization, skin management, daytime sleeping, pearl teleportation, and player riding features.",
  "authors": [
    "Brokeyuan"
  ],
  "contact": {
    "sources": "https://github.com/brokeyuan/Carpet-Primaryuan-Addition"
  },

  "license": "LGPL-3.0",
  "icon": "icon.png",

  "environment": "*",
  "entrypoints": {
    "main": [
      "me.primaryuan.carpet.CarpetPrimaryuanMod"
    ]
  },
  "mixins": [
    "carpet-primaryuan.mixins.json"
  ],
  "accessWidener": "carpet-primaryuan.accesswidener",

  "depends": {
    "fabricloader": ">=0.16.0",
    "fabric-api": "*",
    "carpet": "${carpet_dependency}",
    "minecraft": "${minecraft_dependency}"
  },
  "suggests": {
    "skinrestorer": "${skinrestorer_dependency}",
    "xaerolib": "*"
  },
  "custom": {
    "modmenu": {
      "parent": "carpet",
      "update_checker": true
    }
  }
}
```

---

## 修改记录

| 日期 | 修改内容 | 状态 |
|------|----------|------|
| 2026-07-20 | 初始版本 | 待审核 |
| 2026-07-20 | 优化 description、启用 update_checker、添加 QQ群链接 | ✅ 已完成 |
