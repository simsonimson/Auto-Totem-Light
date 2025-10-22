package simson.smartoffhand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simson.smartoffhand.config.SmartOffhandConfig;
import simson.smartoffhand.hud.SmartOffhandHud;
import simson.smartoffhand.hud.SmartOffhandHudRenderer;

/**
 * Client-side mod class for SmartOffhand - FULL APPLICATION VERSION
 * Complete AutoTotem Light functionality with health monitoring and totem swapping
 */
public class SmartOffhandClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartOffhand");
    
    private KeyBinding totemSwapKey;
    private SmartOffhandHud hud;
    private boolean clientReady = false;
    private boolean keybindingRegistered = false;
    private long lastDamageTime = 0;
    private boolean wasLowHealth = false;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("=== SmartOffhandClient LOADED! ===");
        
        try {
            // Initialize HUD
            hud = new SmartOffhandHud();
            
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
            
            // Register HUD renderer
            HudRenderCallback.EVENT.register(new SmartOffhandHudRenderer(hud)::render);
            
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
            
            LOGGER.info("SmartOffhand client initialized successfully - FULL APPLICATION");
            
        } catch (Exception e) {
            LOGGER.error("CRITICAL ERROR: Failed to initialize SmartOffhand client", e);
            // Don't rethrow - let the mod continue loading
        }
    }
    
    /**
     * Handle client tick events - FULL APPLICATION VERSION
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
            // Get configuration
            SmartOffhandConfig config = SmartOffhandConfig.getInstance();
            if (!config.isEnabled()) {
                return;
            }
            
            PlayerEntity player = client.player;
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();
            
            // Check if health is below threshold
            boolean isLowHealth = config.isHealthBelowThreshold(currentHealth, maxHealth);
            
            // Check if we should only trigger during combat
            boolean shouldTrigger = isLowHealth;
            if (config.fightOnly) {
                long currentTime = client.world.getTime();
                shouldTrigger = isLowHealth && (currentTime - lastDamageTime) < 100; // 5 seconds
            }
            
            // Check if player already has totem in offhand
            boolean hasTotemInOffhand = player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING;
            
            // Update HUD state
            if (shouldTrigger && !hasTotemInOffhand) {
                hud.setReadyToSwap(true);
                if (!wasLowHealth) {
                    // Just became low health
                    if (config.lowHealthSound) {
                        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
                    }
                }
            } else {
                hud.setReadyToSwap(false);
            }
            
            wasLowHealth = isLowHealth;
            
            // Handle keybinding with proper while loop
            while (totemSwapKey.wasPressed()) {
                handleTotemSwap(client, player, config);
            }
            
        } catch (Exception e) {
            LOGGER.error("Error in client tick", e);
        }
    }
    
    /**
     * Handle totem swap when key is pressed - FULL APPLICATION VERSION
     */
    private void handleTotemSwap(MinecraftClient client, PlayerEntity player, SmartOffhandConfig config) {
        try {
            // Check if player already has totem in offhand
            if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
                player.sendMessage(Text.literal("§aTotem already equipped!"), true);
                return;
            }
            
            // Search for totem based on priority
            ItemStack foundTotem = findTotem(player, config.priority);
            
            if (foundTotem != null && !foundTotem.isEmpty()) {
                // Find the original slot first
                int totemSlot = -1;
                for (int i = 0; i < player.getInventory().size(); i++) {
                    if (ItemStack.areEqual(player.getInventory().getStack(i), foundTotem)) {
                        totemSlot = i;
                        break;
                    }
                }
                
                if (totemSlot != -1) {
                    // Store the current offhand item BEFORE making any changes
                    ItemStack previousOffhand = player.getOffHandStack().copy();
                    ItemStack totemStack = player.getInventory().getStack(totemSlot).copy();
                    
                    // SWAP: Put totem in offhand and put previous offhand item where totem was
                    player.setStackInHand(Hand.OFF_HAND, totemStack);
                    player.getInventory().setStack(totemSlot, previousOffhand);
                    
                    // Force inventory sync
                    player.getInventory().markDirty();
                    
                    // Force the player to update their equipment
                    player.updateTrackedPosition(player.getX(), player.getY(), player.getZ());
                }
                
                // Play success sound and show message
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.5f, 1.0f);
                player.sendMessage(Text.literal("§aTotem equipped!"), true);
                
                // Update HUD
                hud.setReadyToSwap(false);
            } else {
                // No totem found - handle based on config
                handleMissingTotem(client, player, config);
            }
            
        } catch (Exception e) {
            LOGGER.error("Error handling totem swap", e);
        }
    }
    
    /**
     * Find totem in inventory based on priority
     */
    private ItemStack findTotem(PlayerEntity player, SmartOffhandConfig.Priority priority) {
        switch (priority) {
            case HOTBAR_FIRST:
                // Search hotbar first (slots 0-8)
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                        return stack;
                    }
                }
                // Then search rest of inventory
                for (int i = 9; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                        return stack;
                    }
                }
                break;
                
            case INVENTORY_ONLY:
                // Search only inventory (slots 9+)
                for (int i = 9; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                        return stack;
                    }
                }
                break;
                
            case IGNORE_INVENTORY:
                // Search only hotbar (slots 0-8)
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                        return stack;
                    }
                }
                break;
        }
        
        return null;
    }
    
    /**
     * Handle missing totem behavior
     */
    private void handleMissingTotem(MinecraftClient client, PlayerEntity player, SmartOffhandConfig config) {
        switch (config.missingTotemBehavior) {
            case SOUND:
                player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 0.5f, 0.5f);
                break;
            case TEXT:
                player.sendMessage(Text.literal("§cNo Totem Available!"), true);
                break;
            case BOTH:
                player.playSound(SoundEvents.ENTITY_VILLAGER_NO, 0.5f, 0.5f);
                player.sendMessage(Text.literal("§cNo Totem Available!"), true);
                break;
            case NONE:
                // Do nothing
                break;
        }
    }
    
    /**
     * Record damage time for fight-only mode
     */
    public void recordDamage() {
        lastDamageTime = System.currentTimeMillis();
    }
}