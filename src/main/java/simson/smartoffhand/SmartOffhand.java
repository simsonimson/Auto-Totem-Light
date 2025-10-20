package simson.smartoffhand;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simson.smartoffhand.config.SmartOffhandConfig;

/**
 * Main mod class for SmartOffhand
 * Handles mod initialization and configuration loading
 */
public class SmartOffhand implements ModInitializer {
    public static final String MOD_ID = "smartoffhand";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SmartOffhand mod");
        
        // Initialize configuration
        SmartOffhandConfig.getInstance();
        
        LOGGER.info("SmartOffhand mod initialized successfully");
    }
}



