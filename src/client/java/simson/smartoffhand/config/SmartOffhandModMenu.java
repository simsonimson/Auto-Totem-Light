package simson.smartoffhand.config;

import net.minecraft.text.Text;

/**
 * Simple configuration helper for SmartOffhand
 * Note: Full Mod Menu integration requires Cloth Config and Mod Menu dependencies
 */
public class SmartOffhandModMenu {
    
    /**
     * Get configuration help text
     */
    public static Text getConfigHelp() {
        return Text.literal("SmartOffhand Configuration:\n" +
                "Edit config/smartoffhand.json to modify settings.\n" +
                "Available options:\n" +
                "- enabled: true/false\n" +
                "- hpThreshold: 1.0-20.0 (0 = 30% of max health)\n" +
                "- hotkey: key binding string\n" +
                "- priority: HOTBAR_FIRST, INVENTORY_ONLY, IGNORE_INVENTORY\n" +
                "- lowHealthSound: true/false\n" +
                "- warningText: true/false\n" +
                "- fightOnly: true/false\n" +
                "- missingTotemBehavior: NONE, SOUND, TEXT, BOTH\n" +
                "- hudPosition: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER");
    }
}
