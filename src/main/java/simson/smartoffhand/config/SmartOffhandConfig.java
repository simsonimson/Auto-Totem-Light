package simson.smartoffhand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Configuration management for SmartOffhand mod
 * Handles JSON serialization/deserialization of mod settings
 */
public class SmartOffhandConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartOffhand");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "autototemlight.json");
    
    // Configuration fields
    public boolean enabled = true;
    public double hpThreshold = 6.0;
    public String hotkey = "key.keyboard.g";
    public Priority priority = Priority.HOTBAR_FIRST;
    public boolean lowHealthSound = true;
    public boolean warningText = true;
    public boolean fightOnly = true;
    public MissingTotemBehavior missingTotemBehavior = MissingTotemBehavior.SOUND;
    public HudPosition hudPosition = HudPosition.BOTTOM_RIGHT;
    
    // Internal state
    private static SmartOffhandConfig instance;
    
    public enum Priority {
        HOTBAR_FIRST("Hotbar First"),
        INVENTORY_ONLY("Inventory Only"),
        IGNORE_INVENTORY("Ignore Inventory");
        
        private final String displayName;
        
        Priority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum MissingTotemBehavior {
        NONE("None"),
        SOUND("Sound"),
        TEXT("Text"),
        BOTH("Both");
        
        private final String displayName;
        
        MissingTotemBehavior(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum HudPosition {
        TOP_LEFT("Top Left"),
        TOP_RIGHT("Top Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_RIGHT("Bottom Right"),
        CENTER("Center");
        
        private final String displayName;
        
        HudPosition(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Get the singleton instance of the configuration
     */
    public static SmartOffhandConfig getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }
    
    /**
     * Load configuration from file or create default
     */
    private static SmartOffhandConfig loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                SmartOffhandConfig config = GSON.fromJson(reader, SmartOffhandConfig.class);
                LOGGER.info("Loaded SmartOffhand configuration from file");
                return config;
            } catch (IOException e) {
                LOGGER.error("Failed to load SmartOffhand configuration", e);
            }
        }
        
        LOGGER.info("Creating default SmartOffhand configuration");
        return new SmartOffhandConfig();
    }
    
    /**
     * Save configuration to file
     */
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
            LOGGER.info("Saved SmartOffhand configuration to file");
        } catch (IOException e) {
            LOGGER.error("Failed to save SmartOffhand configuration", e);
        }
    }
    
    /**
     * Reset configuration to defaults
     */
    public void resetToDefaults() {
        this.enabled = true;
        this.hpThreshold = 6.0;
        this.hotkey = "key.keyboard.g";
        this.priority = Priority.HOTBAR_FIRST;
        this.lowHealthSound = true;
        this.warningText = true;
        this.fightOnly = true;
        this.missingTotemBehavior = MissingTotemBehavior.SOUND;
        this.hudPosition = HudPosition.BOTTOM_RIGHT;
        save();
    }
    
    /**
     * Check if the mod is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Check if health is below threshold
     */
    public boolean isHealthBelowThreshold(float currentHealth, float maxHealth) {
        if (hpThreshold <= 0) {
            return currentHealth <= (maxHealth * 0.3f); // 30% of max health
        }
        return currentHealth <= hpThreshold;
    }
}
