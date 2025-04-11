package matchle.util;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import javax.swing.JFrame;

public class UIUtilsTest {
    
    private JFrame testFrame;
    
    @Before
    public void setUp() {
        // 创建测试用的JFrame
        testFrame = new JFrame("Test Frame");
    }
    
    /**
     * 由于UIUtils方法主要是显示对话框，难以进行自动化测试
     * 这些测试主要是测试方法调用不会抛出异常
     */
    
    @Test
    public void testShowInfoMessage() {
        try {
            // 我们无法自动测试对话框是否实际显示，但可以验证方法不抛出异常
            // 在实际测试中，这将显示一个对话框，但在自动化测试环境中可能不会
            UIUtils.showInfoMessage(testFrame, "Test Info Message", "Info Title");
            // 如果执行到这里，表示没有异常
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowWarningMessage() {
        try {
            UIUtils.showWarningMessage(testFrame, "Test Warning Message", "Warning Title");
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowErrorMessage() {
        try {
            UIUtils.showErrorMessage(testFrame, "Test Error Message", "Error Title");
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
    
    @Test
    public void testShowMessagesWithNullParent() {
        try {
            // 测试父组件为null的情况
            UIUtils.showInfoMessage(null, "Test with null parent", "Null Parent Test");
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception should not be thrown with null parent: " + e.getMessage());
        }
    }
} 