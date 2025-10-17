# AutoTotem Light

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.10-brightgreen.svg)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Fabric-0.135.0-blue.svg)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A reactive assist system that alerts players when health is low and prepares for manual Totem swap with hotkey support.

## 🧭 Mod Concept

AutoTotem Light is **not** a cheat-style AutoTotem. It is a **reactive assist system** that alerts the player when health is low and prepares for a **manual Totem swap** — you press a key to instantly equip a Totem of Undying into your offhand when it's critical.

## ⚙️ Core Features

### 1. Health Threshold Detection
- Allow players to define a **custom HP threshold** (e.g., 6 HP or 30% of max health).
- When the player's health falls **below that threshold**, and no Totem is currently equipped in the offhand:
  - Activate a "Ready-to-Swap" state.

### 2. Ready-Swap Warning
- When active, display one or more of the following indicators:
  - **On-screen text:** "Totem Available – Press [G]"
  - **Optional sound cue** (subtle alert)
  - **Optional flash effect or HUD icon**
- The indicator remains until the player's health recovers or the Totem is swapped in.

### 3. Manual Hotkey Swap
- Player presses a **configurable hotkey** (default: `G`).
- When pressed:
  - Search for a **Totem of Undying** first in **hotbar**, then in **inventory** (priority configurable).
  - Instantly move it into **offhand** using `player.setStackInHand(Hand.OFF_HAND, itemStack)`.
  - Play a short feedback sound and show text "Totem equipped."

### 4. Behavior When No Totem Found
- Configurable response:
  - Option 1: Show red text "No Totem found!"
  - Option 2: Play "fail" sound
  - Option 3: Do nothing

## 🧩 Configuration Options

Config file: `config/autototemlight.json`

Example:
```json
{
  "enabled": true,
  "hpThreshold": 6.0,
  "hotkey": "key.keyboard.g",
  "priority": "HOTBAR_FIRST",
  "lowHealthSound": true,
  "warningText": true,
  "fightOnly": true,
  "missingTotemBehavior": "SOUND",
  "hudPosition": "BOTTOM_RIGHT"
}
```

### Config explanation:

- **hpThreshold**: triggers Ready-Swap state when below this HP.
- **hotkey**: user-defined key for manual swap.
- **priority**: "HOTBAR_FIRST", "INVENTORY_ONLY", or "IGNORE_INVENTORY".
- **lowHealthSound**: whether to play a sound when critical.
- **warningText**: show text overlay hint.
- **fightOnly**: only trigger Ready-Swap when the player has recently taken damage.
- **missingTotemBehavior**: defines alert type if no Totem found.
- **hudPosition**: controls where the message appears.

## 🧠 Implementation Details

- Uses `ClientTickEvents.END_CLIENT_TICK` for health checks.
- Uses `MinecraftClient.getInstance().player.getHealth()` to read health.
- Detects "recent damage" via a simple cooldown counter reset on `LivingEntityHurtCallback`.
- Uses `KeyBinding` for manual swap.
- Search logic:
  - Iterate through player hotbar slots first, then inventory if allowed.
  - When Totem found, move it into offhand using:
    ```java
    player.getInventory().setStack(slot, ItemStack.EMPTY);
    player.setStackInHand(Hand.OFF_HAND, foundTotem);
    ```
  - Break loop once swap succeeds.
- Display HUD text using `InGameHud` overlay.
- Uses `SoundEvents.UI_BUTTON_CLICK` or `ENTITY_EXPERIENCE_ORB_PICKUP` for subtle feedback.

## 🖥️ Configuration

The mod uses JSON configuration files located in `config/smartoffhand.json`. You can edit this file directly or use the in-game configuration system.

### Available Settings:

- **enabled**: Enable/disable the mod
- **hpThreshold**: Health threshold (1.0-20.0, 0 = 30% of max health)
- **hotkey**: Key binding for totem swap (default: G)
- **priority**: Search priority (HOTBAR_FIRST, INVENTORY_ONLY, IGNORE_INVENTORY)
- **lowHealthSound**: Play sound when health becomes low
- **warningText**: Show on-screen warning text
- **fightOnly**: Only trigger when player has recently taken damage
- **missingTotemBehavior**: What to do when no totem is found (NONE, SOUND, TEXT, BOTH)
- **hudPosition**: Position of warning text (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER)

## 🌟 UX Notes

- Keep the on-screen warning subtle and clean — small text, bottom right corner.
- Use smooth fade-in/out transitions for the alert text.
- Sound volume should be quiet and non-intrusive.
- The system should never equip items automatically — only on hotkey press.

## 📦 Installation

### Prerequisites
- **Minecraft**: 1.21.10
- **Fabric Loader**: 0.17.2 or later
- **Fabric API**: 0.135.0+1.21.10
- **Java**: 21 or later

### Download
- **Latest Release**: [Download from Releases](releases/autototemlight-1.0.0-mc1.21.10.jar)

### Installation Steps
1. Download the mod JAR file from the releases section
2. Place it in your `mods` folder
3. Make sure you have Fabric Loader and Fabric API installed
4. Launch Minecraft 1.21.10

## 🔧 Building from Source

1. Clone the repository
2. Run `./gradlew build` (Linux/Mac) or `gradlew.bat build` (Windows)
3. The built JAR will be in `build/libs/`

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

If you encounter any issues or have questions, please open an issue on the GitHub repository.
