package matchle.exception;

import static org.junit.Assert.*;
import org.junit.Test;

public class MatchleExceptionTest {
    
    private static final String TEST_MESSAGE = "Test exception message";
    private static final Exception TEST_CAUSE = new RuntimeException("Test cause");
    
    @Test
    public void testConstructorWithMessage() {
        MatchleException exception = new MatchleException(TEST_MESSAGE);
        
        assertEquals("Exception message should match", TEST_MESSAGE, exception.getMessage());
        assertNull("Cause should be null", exception.getCause());
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        MatchleException exception = new MatchleException(TEST_MESSAGE, TEST_CAUSE);
        
        assertEquals("Exception message should match", TEST_MESSAGE, exception.getMessage());
        assertSame("Cause should match", TEST_CAUSE, exception.getCause());
    }
    
    @Test
    public void testExceptionHierarchy() {
        MatchleException exception = new MatchleException(TEST_MESSAGE);
        
        // 验证异常层次结构
        assertTrue("MatchleException should be a RuntimeException", 
                exception instanceof RuntimeException);
    }
} 