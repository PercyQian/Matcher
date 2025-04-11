package matchle.exception;

import static org.junit.Assert.*;
import org.junit.Test;

public class CorpusExceptionTest {
    
    private static final String TEST_MESSAGE = "Test corpus exception";
    private static final Exception TEST_CAUSE = new RuntimeException("Test cause");
    
    @Test
    public void testConstructorWithMessage() {
        CorpusException exception = new CorpusException(TEST_MESSAGE);
        
        assertEquals("Exception message should match", TEST_MESSAGE, exception.getMessage());
        assertNull("Cause should be null", exception.getCause());
    }
    
    @Test
    public void testConstructorWithMessageAndCause() {
        CorpusException exception = new CorpusException(TEST_MESSAGE, TEST_CAUSE);
        
        assertEquals("Exception message should match", TEST_MESSAGE, exception.getMessage());
        assertSame("Cause should match", TEST_CAUSE, exception.getCause());
    }
    
    @Test
    public void testExceptionHierarchy() {
        CorpusException exception = new CorpusException(TEST_MESSAGE);
        
        // 验证异常层次结构
        assertTrue("CorpusException should be a MatchleException", 
                exception instanceof MatchleException);
        assertTrue("CorpusException should be a RuntimeException", 
                exception instanceof RuntimeException);
    }
    
    @Test
    public void testEmptyCorpusException() {
        CorpusException.EmptyCorpusException exception = new CorpusException.EmptyCorpusException();
        
        // 验证异常消息包含特定文本
        assertTrue("Exception message should contain 'empty'", 
                exception.getMessage().toLowerCase().contains("empty"));
        
        // 验证异常层次结构
        assertTrue("EmptyCorpusException should be a CorpusException", 
                exception instanceof CorpusException);
    }
    
    @Test
    public void testInconsistentWordSizeException() {
        CorpusException.InconsistentWordSizeException exception = 
                new CorpusException.InconsistentWordSizeException();
        
        // 验证异常消息包含特定文本
        assertTrue("Exception message should contain 'inconsistent'", 
                exception.getMessage().toLowerCase().contains("inconsistent"));
        
        // 验证异常层次结构
        assertTrue("InconsistentWordSizeException should be a CorpusException", 
                exception instanceof CorpusException);
    }
} 