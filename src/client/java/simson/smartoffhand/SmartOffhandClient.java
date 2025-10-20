package simson.smartoffhand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod class for SmartOffhand - MINIMAL SAFE VERSION
 * Only registers keybinding and shows test message on press
 */
public class SmartOffhandClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartOffhand");
    
    private KeyBinding totemSwapKey;
    private boolean initialized = false;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("=== SmartOffhandClient LOADED! ===");
        
        try {
            // Register keybinding immediately - safe approach
            totemSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototemlight.totem_swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.misc"
            ));
            
            LOGGER.info("Keybinding registered: {} with category: {}", 
                totemSwapKey.getTranslationKey(), totemSwapKey.getCategory());
            
            // Register client tick event with safe null checks
            ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
            
            initialized = true;
            LOGGER.info("SmartOffhand client initialized successfully - MINIMAL VERSION");
            
        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR: Failed to initialize SmartOffhand client", e);
            // Don't rethrow - let the mod continue loading
        }
    }
    
    /**
     * Handle client tick events - SAFE VERSION
     */
    private void onClientTick(MinecraftClient client) {
        // Early returns for safety
        if (!initialized || totemSwapKey == null) {
            return;
        }
        
        if (client == null) {
            return;
        }
        
        // Only proceed if player and world exist
        if (client.player == null || client.world == null) {
            return;
        }
        
        try {
            // Handle keybinding with proper while loop for Lunar Client compatibility
            while (totemSwapKey.wasPressed()) {
                handleKeyPress(client);
            }
        } catch (Exception e) {
            LOGGER.error("Error in client tick", e);
        }
    }
    
    /**
     * Handle key press - MINIMAL TEST VERSION
     */
    private void handleKeyPress(MinecraftClient client) {
        try {
            if (client.player != null) {
                // Simple test message
                client.player.sendMessage(Text.literal("§aTest OK - Keybinding works!"), true);
                LOGGER.info("Keybinding pressed - Test message sent");
            }
        } catch (Exception e) {
            LOGGER.error("Error handling key press", e);
        }
    }
}