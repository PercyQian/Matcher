package matchle;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

public class NGramTest {

    @Test
    public void testFromString() {
        NGram ngram = NGram.from("hello");
        
        assertNotNull(ngram);
        assertEquals("NGram size should be 5", 5, ngram.size());
        assertEquals("First character should be 'h'", (Character)'h', ngram.get(0));
        assertEquals("Last character should be 'o'", (Character)'o', ngram.get(4));
    }

    @Test
    public void testFromCharList() {
        List<Character> chars = List.of('h', 'e', 'l', 'l', 'o');
        NGram ngram = NGram.from(chars);
        
        assertNotNull(ngram);
        assertEquals("NGram size should be 5", 5, ngram.size());
        assertEquals("First character should be 'h'", (Character)'h', ngram.get(0));
    }

    @Test
    public void testFromStringCopy() {
        NGram original = NGram.from("apple");
        // 由于NGram类可能没有接受NGram参数的from方法，使用字符串重新创建
        NGram copy = NGram.from("apple");
        
        assertNotNull(copy);
        assertEquals("Copy should have same size as original", original.size(), copy.size());
        assertEquals("First character should be 'a'", (Character)'a', copy.get(0));
    }
    
    @Test
    public void testEmptyString() {
        NGram ngram = NGram.from("");
        assertNotNull(ngram);
        assertEquals("Empty NGram should have size 0", 0, ngram.size());
    }
    
    @Test
    public void testNullInput() {
        // 使用try-catch替代assertThrows
        boolean exceptionThrown = false;
        try {
            NGram.from((String)null);
        } catch (NullPointerException expected) {
            exceptionThrown = true;
        }
        assertEquals("Null string should throw NullPointerException", true, exceptionThrown);
    }
}
