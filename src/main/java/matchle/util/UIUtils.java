package matchle.util;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * utility class for UI operations
 */
public final class UIUtils {
    
    private UIUtils() {
        // prevent instantiation
    }
    
    /**
     * show the information dialog
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * show the warning dialog
     */
    public static void showWarningMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * show the error dialog
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
} 