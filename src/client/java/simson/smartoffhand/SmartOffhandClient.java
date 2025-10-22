package simson.smartoffhand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod class for SmartOffhand - CRASH-SAFE VERSION
 * Uses ClientPlayConnectionEvents for safe initialization
 */
public class SmartOffhandClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartOffhand");
    
    private KeyBinding totemSwapKey;
    private boolean clientReady = false;
    private boolean keybindingRegistered = false;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("=== SmartOffhandClient LOADED! ===");
        
        try {
            // Register keybinding immediately - this is safe
            totemSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototemlight.totem_swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.misc"
            ));
            
            keybindingRegistered = true;
            LOGGER.info("Keybinding registered: {} with category: {}", 
                totemSwapKey.getTranslationKey(), totemSwapKey.getCategory());
            
            // Register client tick event - safe with null checks
            ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
            
            // Register join event for safe initialization
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                LOGGER.info("Player joined world - SmartOffhand is now active");
                clientReady = true;
            });
            
            // Register disconnect event
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
                LOGGER.info("Player left world - SmartOffhand deactivated");
                clientReady = false;
            });
            
            LOGGER.info("SmartOffhand client initialized successfully - CRASH-SAFE VERSION");
            
        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR: Failed to initialize SmartOffhand client", e);
            // Don't rethrow - let the mod continue loading
        }
    }
    
    /**
     * Handle client tick events - ULTRA SAFE VERSION
     */
    private void onClientTick(MinecraftClient client) {
        // Early returns for maximum safety
        if (!keybindingRegistered || totemSwapKey == null) {
            return;
        }
        
        if (client == null) {
            return;
        }
        
        // Only proceed if client is ready and player exists
        if (!clientReady) {
            return;
        }
        
        if (client.player == null || client.world == null) {
            return;
        }
        
        try {
            // Handle keybinding with proper while loop
            while (totemSwapKey.wasPressed()) {
                handleKeyPress(client);
            }
        } catch (Exception e) {
            LOGGER.error("Error in client tick", e);
        }
    }
    
    /**
     * Handle key press - SAFE TEST VERSION
     */
    private void handleKeyPress(MinecraftClient client) {
        try {
            if (client.player != null) {
                // Test message with current health info
                float health = client.player.getHealth();
                float maxHealth = client.player.getMaxHealth();
                
                String message = String.format("§aTest OK! Health: %.1f/%.1f", health, maxHealth);
                client.player.sendMessage(Text.literal(message), true);
                LOGGER.info("Keybinding pressed - Health: {}/{}", health, maxHealth);
            }
        } catch (Exception e) {
            LOGGER.error("Error handling key press", e);
        }
    }
}