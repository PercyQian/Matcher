package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 测试NullCharacterException的功能
 */
public class NullCharacterExceptionTest {
    
    @Test
    public void testConstructorAndGetIndex() {
        // 测试构造函数和getIndex方法
        int testIndex = 5;
        NullCharacterException exception = new NullCharacterException(testIndex);
        
        // 验证索引被正确保存
        assertEquals("Index should be stored correctly", testIndex, exception.getIndex());
        
        // 验证错误消息包含索引
        assertTrue("Message should contain the index", 
                exception.getMessage().contains(String.valueOf(testIndex)));
    }
    
    @Test
    public void testValidateValidList() {
        // 测试validate方法对有效列表的处理
        List<Character> validList = Arrays.asList('a', 'b', 'c', 'd');
        
        // 对有效列表进行验证，不应抛出异常
        List<Character> result = NullCharacterException.validate(validList);
        
        // 验证返回的列表与输入相同
        assertSame("Validate should return the same list for valid input", validList, result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidateWithNullCharacter() {
        // 测试validate方法对包含null字符的列表的处理
        List<Character> listWithNull = new ArrayList<>();
        listWithNull.add('a');
        listWithNull.add(null);  // 添加一个null字符
        listWithNull.add('c');
        
        // 这应该抛出IllegalArgumentException，包装了NullCharacterException
        NullCharacterException.validate(listWithNull);
    }
    
    @Test(expected = NullPointerException.class)
    public void testValidateWithNullList() {
        // 测试validate方法对null列表的处理
        NullCharacterException.validate(null);
    }
    
    @Test
    public void testExceptionCause() {
        // 测试异常的cause链
        List<Character> listWithNull = new ArrayList<>();
        listWithNull.add('a');
        listWithNull.add(null);  // 索引1处的null字符
        
        try {
            NullCharacterException.validate(listWithNull);
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // 验证异常的cause是NullCharacterException
            assertTrue("Cause should be NullCharacterException", 
                    e.getCause() instanceof NullCharacterException);
            
            // 验证索引值
            NullCharacterException cause = (NullCharacterException) e.getCause();
            assertEquals("Index should be 1", 1, cause.getIndex());
        }
    }
    
    @Test
    public void testMultipleNullCharacters() {
        // 测试包含多个null字符的列表，应该在第一个null处抛出异常
        List<Character> listWithMultipleNulls = new ArrayList<>();
        listWithMultipleNulls.add('a');
        listWithMultipleNulls.add(null);  // 索引1处的null字符
        listWithMultipleNulls.add('c');
        listWithMultipleNulls.add(null);  // 索引3处的null字符
        
        try {
            NullCharacterException.validate(listWithMultipleNulls);
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // 验证异常指向第一个null字符
            NullCharacterException cause = (NullCharacterException) e.getCause();
            assertEquals("Should report the first null at index 1", 1, cause.getIndex());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullAtFirstPosition() {
        // 测试首位置的null字符
        List<Character> listWithFirstNull = new ArrayList<>();
        listWithFirstNull.add(null);  // 索引0处的null字符
        listWithFirstNull.add('b');
        listWithFirstNull.add('c');
        
        NullCharacterException.validate(listWithFirstNull);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullAtLastPosition() {
        // 测试末位置的null字符
        List<Character> listWithLastNull = new ArrayList<>();
        listWithLastNull.add('a');
        listWithLastNull.add('b');
        listWithLastNull.add(null);  // 索引2处的null字符
        
        NullCharacterException.validate(listWithLastNull);
    }
} 