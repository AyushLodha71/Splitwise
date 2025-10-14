package splitwiseapplication;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 * ResourceManager - Singleton class for loading and caching application resources.
 * 
 * This class ensures that icons and other resources are loaded only once and reused
 * throughout the application, improving performance and reducing network calls.
 * 
 * @version 2.0
 */
public class ResourceManager {
    
    // Singleton instance
    private static ResourceManager instance;
    
    // Cached icons for password visibility toggle
    private ImageIcon openEyeIcon;
    private ImageIcon closedEyeIcon;
    
    /**
     * Private constructor to enforce singleton pattern.
     * Loads all required icons when the instance is first created.
     */
    private ResourceManager() {
        loadIcons();
    }
    
    /**
     * Gets the singleton instance of ResourceManager.
     * Creates the instance on first call (lazy initialization).
     * 
     * @return The single ResourceManager instance
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    /**
     * Loads all application icons from remote URLs or local fallback.
     * This method is called once during initialization.
     */
    private void loadIcons() {
        try {
            // Load eye icons for password visibility toggle
            openEyeIcon = new ImageIcon(new URL("https://img.icons8.com/material/24/000000/visible.png"));
            closedEyeIcon = new ImageIcon(new URL("https://img.icons8.com/material/24/000000/invisible.png"));
            
            System.out.println("Icons loaded successfully from remote URLs");
        } catch (Exception e) {
            System.err.println("Error loading icons from URLs: " + e.getMessage());
            // Create empty icons as fallback to prevent NullPointerExceptions
            openEyeIcon = new ImageIcon();
            closedEyeIcon = new ImageIcon();
        }
    }
    
    /**
     * Gets the "eye open" icon for showing password.
     * 
     * @return ImageIcon for visible password state
     */
    public ImageIcon getOpenEyeIcon() {
        return openEyeIcon;
    }
    
    /**
     * Gets the "eye closed" icon for hiding password.
     * 
     * @return ImageIcon for hidden password state
     */
    public ImageIcon getClosedEyeIcon() {
        return closedEyeIcon;
    }
}
