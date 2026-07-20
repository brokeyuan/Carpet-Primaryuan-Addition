# Carpet Pry Addition Rules Documentation

> Mod ID: `carpet-pry-addition` | Version: `1.1.2`
>
> **Tip: Use `Ctrl+F` to quickly find the rule you want**

---

## Quick Navigation

- [Fake Player](#fake-player)
  - [TppFakePlayer - Fake Player Pearl Station Teleport](#tppfakeplayer---fake-player-pearl-station-teleport)
  - [fakePlayerNameSuggestions - Fake Player Name Suggestions](#fakeplayernamesuggestions---fake-player-name-suggestions)
  - [fakePlayerSkinMode - Fake Player Skin Setting](#fakeplayerskinmode---fake-player-skin-setting)
  - [fakePlayerSkinSet - Fake Player Unified Skin Setting](#fakeplayerskinset---fake-player-unified-skin-setting)
  - [xaerolibFix - Fake Player Data XaeroLib Fix](#xaerolibfix---fake-player-data-xaerolib-fix)
- [Player Interaction](#player-interaction)
  - [ridingPlayers - Riding Players](#ridingplayers---riding-players)
  - [pickupPlayers - Picking Up Players](#pickupplayers---picking-up-players)
  - [ridingPlayersPickUpLimit - Player Riding Stack Limit](#ridingplayerspickuplimit---player-riding-stack-limit)
  - [ridingPlayersDismountOnGameModeChange - Dismount on Game Mode Change](#ridingplayersdismountongamemodechange---dismount-on-game-mode-change)
  - [ridingPlayersClientAllowInteractions - Allow Interaction While Riding (Client)](#ridingplayersclientallowinteractions---allow-interaction-while-riding-client)
- [Survival Features](#survival-features)
  - [sleepingDuringTheDay - Daydreaming](#sleepingduringtheday---daydreaming)
  - [betterSnowBall - Better Snowball](#bettersnowball---better-snowball)
  - [playerhat - Player Hat](#playerhat---player-hat)
  - [invisibleInTallGrass - Invisibility Grass](#invisibleintallgrass---invisibility-grass)
- [Other Features](#other-features)
  - [unicodeArgumentsSupport - Unicode Argument Support](#unicodeargumentssupport---unicode-argument-support)

---

## Fake Player

### TppFakePlayer - Fake Player Pearl Station Teleport

Use fake players to quickly use pearl teleport stations. When set to true, enables the /tppset setup command and the /tpp player command.

| Property | Value |
|----------|-------|
| **Rule Name** | `TppFakePlayer` |
| **Description** | Use fake players to quickly use pearl teleport stations. When set to true, enables the /tppset setup command and the /tpp player command |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `CREATIVE`, `SURVIVAL` |

---

### fakePlayerNameSuggestions - Fake Player Name Suggestions

Customize the fake player list suggested by /player. Use ',' to separate each name.

| Property | Value |
|----------|-------|
| **Rule Name** | `fakePlayerNameSuggestions` |
| **Description** | Customize the fake player list suggested by /player. Use ',' to separate each name |
| **Type** | `string` |
| **Default Value** | `Steve,Alex` |
| **Suggested Options** | `Steve,Alex`, `Pry,hsds`, `Pry,hsds,Firework,Food`, `` |
| **Categories** | `PRIMARYUAN`, `CREATIVE`, `SURVIVAL` |

---

### fakePlayerSkinMode - Fake Player Skin Setting

Set the skin of fake players. default=no change, summon=use the summoner's skin, same_skin=use a unified skin.

| Property | Value |
|----------|-------|
| **Rule Name** | `fakePlayerSkinMode` |
| **Description** | Set the skin of fake players. default=no change, summon=use the summoner's skin, same_skin=use a unified skin |
| **Type** | `string` |
| **Default Value** | `default` |
| **Suggested Options** | `default`, `summon`, `same_skin` |
| **Categories** | `PRIMARYUAN`, `CREATIVE`, `SURVIVAL` |

#### Mode Description

| Mode | Behavior |
|------|----------|
| `default` | No skin change |
| `summon` | Use the summoner's skin |
| `same_skin` | Use a unified skin |

---

### fakePlayerSkinSet - Fake Player Unified Skin Setting

When FakeplayersSkinMode is same_skin, sets the player name used for the fake player skin.

| Property | Value |
|----------|-------|
| **Rule Name** | `fakePlayerSkinSet` |
| **Description** | When FakeplayersSkinMode is same_skin, sets the player name used for the fake player skin |
| **Type** | `string` |
| **Default Value** | `Brokeyuan` |
| **Suggested Options** | `Brokeyuan`, `hsds`, `` |
| **Categories** | `PRIMARYUAN`, `CREATIVE`, `SURVIVAL` |

---

### xaerolibFix - Fake Player Data XaeroLib Fix

Fixes the issue where higher versions of Xaero combined with LuckPerms cause fake player data loss.

| Property | Value |
|----------|-------|
| **Rule Name** | `xaerolibFix` |
| **Description** | Fixes the issue where higher versions of Xaero combined with LuckPerms cause fake player data loss |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `FEATURE` |

---

## Player Interaction

### ridingPlayers - Riding Players

When holding a Totem of Undying in the main hand, you can ride other players.

| Property | Value |
|----------|-------|
| **Rule Name** | `ridingPlayers` |
| **Description** | When holding a Totem of Undying in the main hand, you can ride other players |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

### pickupPlayers - Picking Up Players

When holding a Totem of Undying in the main hand and a Golden Carrot in the off-hand, you can pick up other players (have them ride on you).

| Property | Value |
|----------|-------|
| **Rule Name** | `pickupPlayers` |
| **Description** | When holding a Totem of Undying in the main hand and a Golden Carrot in the off-hand, you can pick up other players (have them ride on you) |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

### ridingPlayersPickUpLimit - Player Riding Stack Limit

The maximum number of players that can be stacked when riding and picking up. This limit is shared between riding and picking up.

| Property | Value |
|----------|-------|
| **Rule Name** | `ridingPlayersPickUpLimit` |
| **Description** | The maximum number of players that can be stacked when riding and picking up. This limit is shared between riding and picking up |
| **Type** | `int` |
| **Default Value** | `16` |
| **Suggested Options** | `16`, `32`, `` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

### ridingPlayersDismountOnGameModeChange - Dismount on Game Mode Change

When a player's game mode changes, players on top will dismount.

| Property | Value |
|----------|-------|
| **Rule Name** | `ridingPlayersDismountOnGameModeChange` |
| **Description** | When a player's game mode changes, players on top will dismount |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

### ridingPlayersClientAllowInteractions - Allow Interaction While Riding (Client)

Requires client installation. When there are passengers on top, you can still interact with blocks/entities.

| Property | Value |
|----------|-------|
| **Rule Name** | `ridingPlayersClientAllowInteractions` |
| **Description** | Requires client installation. When there are passengers on top, you can still interact with blocks/entities |
| **Type** | `boolean` |
| **Default Value** | `true` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE`, `CLIENT` |

---

## Survival Features

### sleepingDuringTheDay - Daydreaming

Allows players to sleep during the day. After sleeping, the time switches to night (referenced from PCA).

| Property | Value |
|----------|-------|
| **Rule Name** | `sleepingDuringTheDay` |
| **Description** | Allows players to sleep during the day. After sleeping, the time switches to night (referenced from PCA) |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `CREATIVE`, `SURVIVAL` |

---

### betterSnowBall - Better Snowball

Stones in snow. Allows snowballs to deal knockback and damage to players.

| Property | Value |
|----------|-------|
| **Rule Name** | `betterSnowBall` |
| **Description** | Stones in snow. Allows snowballs to deal knockback and damage to players |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

### playerhat - Player Hat

Allows players to wear items on their head and adds the /hat command. When a Totem of Undying is placed in the head slot, the death protection effect is triggered.

| Property | Value |
|----------|-------|
| **Rule Name** | `playerhat` |
| **Description** | Allows players to wear items on their head and adds the /hat command. When a Totem of Undying is placed in the head slot, the death protection effect is triggered |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

### invisibleInTallGrass - Invisibility Grass

Automatically makes the player invisible when their head is located in tall grass.

| Property | Value |
|----------|-------|
| **Rule Name** | `invisibleInTallGrass` |
| **Description** | Automatically makes the player invisible when their head is located in tall grass |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `SURVIVAL`, `FEATURE` |

---

## Other Features

### unicodeArgumentsSupport - Unicode Argument Support

Allows the use of non-ASCII characters in command arguments (Chinese, Japanese, Korean, etc., can be used to summon fake players with Chinese names) (ported from YACA).

| Property | Value |
|----------|-------|
| **Rule Name** | `unicodeArgumentsSupport` |
| **Description** | Allows the use of non-ASCII characters in command arguments (Chinese, Japanese, Korean, etc., can be used to summon fake players with Chinese names) (ported from YACA) |
| **Type** | `boolean` |
| **Default Value** | `false` |
| **Suggested Options** | `false`, `true` |
| **Categories** | `PRIMARYUAN`, `FEATURE` |
