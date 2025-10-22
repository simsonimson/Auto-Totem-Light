package simson.smartoffhand.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HUD renderer for SmartOffhand warning text - CRASH-SAFE VERSION
 * Handles rendering with comprehensive null checks
 */
public class SmartOffhandHudRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SmartOffhand");
    private final SmartOffhandHud hud;
    
    public SmartOffhandHudRenderer(SmartOffhandHud hud) {
        this.hud = hud;
    }
    
    /**
     * Render the HUD overlay - ULTRA SAFE VERSION
     */
    public void render(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        try {
            // Early null checks
            if (context == null || hud == null) {
                return;
            }
            
            if (!hud.shouldRender()) {
                return;
            }
            
            // Safe client access
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) {
                return;
            }
            
            if (client.player == null || client.world == null) {
                return;
            }
            
            if (client.getWindow() == null) {
                return;
            }
            
            // Safe screen dimension access
            int screenWidth, screenHeight;
            try {
                screenWidth = client.getWindow().getScaledWidth();
                screenHeight = client.getWindow().getScaledHeight();
            } catch (Exception e) {
                LOGGER.warn("Failed to get screen dimensions", e);
                return;
            }
            
            // Calculate position (simplified for safety)
            int x = screenWidth - 200;
            int y = screenHeight - 30;
            
            // Get current alpha safely
            float alpha = hud.getAlpha();
            if (alpha <= 0.0f) {
                return;
            }
            
            // Create warning text
            Text warningText = Text.literal("§6Totem Available – Press G");
            
            // Render with alpha safely
            int color = (int) (alpha * 255) << 24 | 0xFFFFFF;
            
            // Draw background for better visibility
            if (alpha > 0.5f) {
                int bgColor = (int) (alpha * 0.3f * 255) << 24 | 0x000000;
                context.fill(x - 5, y - 2, x + 195, y + 12, bgColor);
            }
            
            // Draw text safely
            if (client.textRenderer != null) {
                context.drawTextWithShadow(client.textRenderer, warningText, x, y, color);
            }
            
        } catch (Exception e) {
            LOGGER.error("Error rendering HUD", e);
        }
    }
}