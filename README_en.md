# Carpet PRY Addition

> **Carpet Mod Extension: Fake Player Enhancement Toolset** — Provides name customization, skin settings, daytime sleeping, pearl teleportation, entity riding and other features for the Carpet fake player system.

| | |
|---|---|
| **Mod ID** | `carpet-pry-addition` |
| **Version** | 1.1.2-SNAPSHOT |
| **Author** | Brokeyuan |
| **Source** | [GitHub](https://github.com/Brokeyuan/carpet-pry-addition) |
| **License** | [LGPL-3.0](LICENSE) |

---

## Dependencies

| Name | Type | Notes |
|------|------|-------|
| Carpet | Required | [MC百科](https://www.mcmod.cn/class/2361.html) |
| Fabric API | Required | [MC百科](https://www.mcmod.cn/class/3124.html) |
| skinrestorer | Optional | [Modrinth](https://modrinth.com/mod/skinrestorer) (only for skin features) |

## Version Support

This mod uses [FallenBreath/preprocessor](https://github.com/Fallen-Breath/preprocessor) multi-version build system, supporting **9 MC versions** simultaneously:

```
1.21 → 1.21.1 → 1.21.3 → 1.21.4 → 1.21.5 → 1.21.8 → 1.21.10 → 1.21.11 → 26.1.2
```

With mainProject (1.21.11) as the base, cross-version compatibility is achieved through Preprocessor conditional compilation + version-specific source code overrides.

## Documentation

- [Rules](docs/rules.md)
- [Commands](docs/commands.md)

---

## Features

### 1. Fake Player Name Suggestions

Overrides the `/player` command's Tab completion suggestion list, allowing admins to preset commonly used fake player names for quick generation.

**Rule**: `fakePlayerNameSuggestions`

```bash
# Set custom name list
/carpet fakePlayerNameSuggestions Steve,Alex,Brokeyuan,Firework
```

### 2. Fake Player Skin System

Automatically fetch and apply skins from Mojang API when spawning Carpet fake players.

**Rule**: `fakePlayerSkinMode` / `fakePlayerSkinSet`

| Mode | Behavior |
|------|----------|
| `default` | Don't modify skin, use default |
| `summon` | Fake player skin follows the summoner |
| `same_skin` | Fake player skin matches the real player with the same name |

### 3. Daytime Sleeping

Allows players to sleep during the day (non-thunderstorm), waking up at night.

**Rule**: `sleepingDuringTheDay` (Reference PCA)

**Behavior Details**:
1. **Bed Entry**: Bypass vanilla `BedRule.canSleep()` daytime check
2. **During Sleep**: Prevent non-natural wake-ups, maintain sleep state
3. **Natural Wake-up** (sleepTimer >= 100): Set server time to evening (13000 tick)

### 4. Fake Player Pearl Teleport (TPP)

Pearl teleportation system based on Carpet fake players. Players quickly teleport to fake player positions through "stations".

**Rule**: `TppFakePlayer`

**Commands**:
- `/tpp <station>` — Teleport to specified station
- `/tppset` — Station management commands

### 5. Riding Players

Allows players to ride other players. Hold a Totem of Undying in main hand and right-click another player.

**Rule**: `ridingPlayers`

### 6. Pickup Players

Allows players to "pick up" other players onto their heads. Hold Totem of Undying in main hand + Golden Carrot in off-hand and right-click another player.

**Rule**: `pickupPlayers`

### 7. Unicode Arguments Support

Allows Unicode characters in command arguments (e.g., Chinese station names). (Ported from YACA)

**Rule**: `unicodeArgumentsSupport`

---

## Installation & Usage

### Dependency Requirements

| Dependency | Version Requirement | Required |
|------------|--------------------|----------|
| Fabric Loader | ≥ 0.16.0 | Yes |
| Fabric Carpet | ≥ 1.4.x | Yes |
| skinrestorer | ≥ 2.4.1 | No |

### Quick Start

1. Install Fabric Loader + Carpet Mod on the server
2. (Optional) Install skinrestorer for fake player skin features
3. Place `carpet-pry-addition-v1.1.2-mc<version>.jar` in the `mods/` directory
4. Start the server, use `/carpet` command to manage rules:

```bash
# View all PRY rules
/carpet pry

# Enable common features
/carpet fakePlayerNameSuggestions Steve,Alex,MyFake1,MyFake2
/carpet fakePlayerSkinMode summon
/carpet sleepingDuringTheDay true
/carpet TppFakePlayer true
/carpet ridingPlayers true
/carpet pickupPlayers true
```

---

## Building

```bash
# Build all 9 versions
./gradlew build

# Clean and rebuild
./gradlew clean build

# Gather all JARs to build/libs/
./gradlew buildAndGather
```

Output JAR naming format: `carpet-pry-addition-v{mod_version}-mc{mc_version}-SNAPSHOT.jar`

---

## License

This project is open source under the [LGPL-3.0](LICENSE) license.