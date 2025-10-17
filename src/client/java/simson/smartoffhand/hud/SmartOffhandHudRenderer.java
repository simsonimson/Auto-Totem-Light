package simson.smartoffhand.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import simson.smartoffhand.config.SmartOffhandConfig;

/**
 * HUD renderer for SmartOffhand warning text
 * Handles rendering of the totem swap warning
 */
public class SmartOffhandHudRenderer {
    private final SmartOffhandHud hud;
    
    public SmartOffhandHudRenderer(SmartOffhandHud hud) {
        this.hud = hud;
    }
    
    /**
     * Render the HUD overlay
     */
    public void render(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        if (!hud.shouldRender()) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        
        SmartOffhandConfig config = SmartOffhandConfig.getInstance();
        if (!config.isEnabled() || !config.warningText) {
            return;
        }
        
        // Get screen dimensions
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Calculate position based on config
        int x, y;
        switch (config.hudPosition) {
            case TOP_LEFT:
                x = 10;
                y = 10;
                break;
            case TOP_RIGHT:
                x = screenWidth - 200;
                y = 10;
                break;
            case BOTTOM_LEFT:
                x = 10;
                y = screenHeight - 30;
                break;
            case BOTTOM_RIGHT:
                x = screenWidth - 200;
                y = screenHeight - 30;
                break;
            case CENTER:
                x = screenWidth / 2 - 100;
                y = screenHeight / 2 - 10;
                break;
            default:
                x = screenWidth - 200;
                y = screenHeight - 30;
                break;
        }
        
        // Get current alpha
        float alpha = hud.getAlpha();
        
        // Create warning text
        String keyName = getKeyName(config.hotkey);
        Text warningText = Text.literal("§6Totem Available – Press " + keyName);
        
        // Render with alpha
        int color = (int) (alpha * 255) << 24 | 0xFFFFFF; // White text with alpha
        
        // Draw background for better visibility
        if (alpha > 0.5f) {
            int bgColor = (int) (alpha * 0.3f * 255) << 24 | 0x000000; // Semi-transparent black background
            context.fill(x - 5, y - 2, x + 195, y + 12, bgColor);
        }
        
        // Draw text
        context.drawTextWithShadow(client.textRenderer, warningText, x, y, color);
    }
    
    /**
     * Get display name for key
     */
    private String getKeyName(String keyTranslationKey) {
        try {
            // Try to get the key name from the translation key
            if (keyTranslationKey.startsWith("key.keyboard.")) {
                String keyName = keyTranslationKey.substring("key.keyboard.".length());
                return keyName.toUpperCase();
            } else if (keyTranslationKey.startsWith("key.mouse.")) {
                String keyName = keyTranslationKey.substring("key.mouse.".length());
                return "MOUSE " + keyName.toUpperCase();
            }
        } catch (Exception e) {
            // Fallback to default
        }
        
        return "G"; // Default fallback
    }
}
