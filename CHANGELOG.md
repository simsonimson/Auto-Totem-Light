# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2025-10-17

### Added
- Initial release of AutoTotem Light
- Health threshold detection system
- Manual hotkey totem swap (default: G key)
- On-screen warning system with configurable positioning
- Sound feedback system
- JSON-based configuration system
- Support for multiple totem search priorities
- "No Totem Available!" message when no totems found
- Fight-only mode for combat scenarios
- Smooth fade-in/out animations for HUD elements

### Features
- **Health Threshold**: Configurable HP threshold (1.0-20.0, 0 = 30% of max health)
- **Hotkey System**: Customizable key binding for totem swap
- **Search Priority**: HOTBAR_FIRST, INVENTORY_ONLY, IGNORE_INVENTORY
- **HUD Positioning**: 5 different positions (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER)
- **Missing Totem Behavior**: NONE, SOUND, TEXT, BOTH
- **Fight-Only Mode**: Only trigger when player has recently taken damage
- **Sound System**: Low health sound and success/fail feedback

### Technical
- Built for Minecraft 1.21.10
- Compatible with Fabric Loader 0.17.2+
- Requires Java 21+
- Uses Fabric API 0.135.0+1.21.10
- Lunar Client compatible
