package matchle.util;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * UI操作相关的工具类
 */
public final class UIUtils {
    
    private UIUtils() {
        // 防止实例化
    }
    
    /**
     * 显示信息对话框
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 显示警告对话框
     */
    public static void showWarningMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * 显示错误对话框
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
} 