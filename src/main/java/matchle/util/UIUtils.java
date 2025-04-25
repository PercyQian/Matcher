package matchle.util;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Utility class providing common UI operations for the Matchle game interface.
 * <p>
 * This class contains static utility methods for displaying various types of
 * dialog boxes and performing other UI-related operations. It encapsulates
 * the details of Swing dialog creation and configuration to provide a simpler
 * interface for the rest of the application.
 * <p>
 * This class cannot be instantiated, as all methods are static.
 */
public final class UIUtils {
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * This constructor is never called as all methods are static.
     */
    private UIUtils() {
        // prevent instantiation
    }
    
    /**
     * Displays an information dialog box with the specified message and title.
     * <p>
     * This method creates and shows a modal dialog box with an information icon
     * that requires user acknowledgment before proceeding.
     *
     * @param parent The parent component for the dialog box, which determines 
     *               the frame in which the dialog is displayed; can be null
     * @param message The message to display in the dialog box
     * @param title The title of the dialog box
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays a warning dialog box with the specified message and title.
     * <p>
     * This method creates and shows a modal dialog box with a warning icon
     * that requires user acknowledgment before proceeding.
     *
     * @param parent The parent component for the dialog box, which determines 
     *               the frame in which the dialog is displayed; can be null
     * @param message The message to display in the dialog box
     * @param title The title of the dialog box
     */
    public static void showWarningMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Displays an error dialog box with the specified message and title.
     * <p>
     * This method creates and shows a modal dialog box with an error icon
     * that requires user acknowledgment before proceeding.
     *
     * @param parent The parent component for the dialog box, which determines 
     *               the frame in which the dialog is displayed; can be null
     * @param message The message to display in the dialog box
     * @param title The title of the dialog box
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
} 