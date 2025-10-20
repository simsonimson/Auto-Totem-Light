package simson.smartoffhand.hud;

/**
 * HUD state management for SmartOffhand
 * Tracks the current state of the warning display
 */
public class SmartOffhandHud {
    private boolean readyToSwap = false;
    private long lastUpdateTime = 0;
    private float alpha = 0.0f;
    
    /**
     * Set whether the player is ready to swap totem
     */
    public void setReadyToSwap(boolean ready) {
        this.readyToSwap = ready;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Check if ready to swap
     */
    public boolean isReadyToSwap() {
        return readyToSwap;
    }
    
    /**
     * Get current alpha for fade effect
     */
    public float getAlpha() {
        long currentTime = System.currentTimeMillis();
        long timeSinceUpdate = currentTime - lastUpdateTime;
        
        if (readyToSwap) {
            // Fade in over 500ms
            alpha = Math.min(1.0f, timeSinceUpdate / 500.0f);
        } else {
            // Fade out over 300ms
            alpha = Math.max(0.0f, 1.0f - (timeSinceUpdate / 300.0f));
        }
        
        return alpha;
    }
    
    /**
     * Check if HUD should be visible
     */
    public boolean shouldRender() {
        return alpha > 0.0f;
    }
}



