package simson.smartoffhand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding.Category;
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
 * Client-side mod class for SmartOffhand
 * Handles client-side events, keybindings, and HUD rendering
 */
public class SmartOffhandClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartOffhand");
    
    private KeyBinding totemSwapKey;
    private SmartOffhandHud hud;
    private long lastDamageTime = 0;
    private boolean wasLowHealth = false;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing SmartOffhand client");
        
        try {
            // Initialize HUD
            hud = new SmartOffhandHud();
        
        // Register keybinding
        totemSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autototemlight.totem_swap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            Category.MISC
        ));
        
            // Register HUD renderer
            HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
                new SmartOffhandHudRenderer(hud).render(drawContext, tickDelta);
            });
            
            // Register client tick event
            ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
            
            LOGGER.info("SmartOffhand client initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize SmartOffhand client", e);
        }
    }
    
    /**
     * Handle client tick events
     */
    private void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }
        
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
        
        // Handle keybinding
        if (totemSwapKey.wasPressed()) {
            handleTotemSwap(client, player, config);
        }
    }
    
    /**
     * Handle totem swap when key is pressed
     */
    private void handleTotemSwap(MinecraftClient client, PlayerEntity player, SmartOffhandConfig config) {
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
                
                // Move totem to offhand
                player.setStackInHand(Hand.OFF_HAND, totemStack);
                
                // Remove totem from original slot
                player.getInventory().setStack(totemSlot, ItemStack.EMPTY);
                
                // Handle the previous offhand item
                if (!previousOffhand.isEmpty()) {
                    // Try to find an empty slot for the previous offhand item
                    boolean foundEmptySlot = false;
                    for (int i = 0; i < player.getInventory().size(); i++) {
                        if (player.getInventory().getStack(i).isEmpty()) {
                            player.getInventory().setStack(i, previousOffhand);
                            foundEmptySlot = true;
                            break;
                        }
                    }
                    
                    // If no empty slot found, drop the item
                    if (!foundEmptySlot) {
                        player.dropItem(previousOffhand, false);
                    }
                }
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
