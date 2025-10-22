package simson.smartoffhand;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simson.smartoffhand.config.SmartOffhandConfig;

/**
 * Main mod class for SmartOffhand - CRASH-SAFE VERSION
 * Handles mod initialization without dangerous client references
 */
public class SmartOffhand implements ModInitializer {
    public static final String MOD_ID = "autototemlight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("=== SmartOffhand Main LOADED! ===");
        
        try {
            // Initialize configuration safely - only file I/O, no client references
            SmartOffhandConfig.getInstance();
            
            LOGGER.info("SmartOffhand mod initialized successfully - CRASH-SAFE VERSION");
            
        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR: Failed to initialize SmartOffhand main", e);
            // Don't rethrow - let the mod continue loading
        }
    }
}