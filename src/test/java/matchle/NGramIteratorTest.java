package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 测试NGram类的迭代器功能
 */
public class NGramIteratorTest {
    
    @Test
    public void testIterator() {
        NGram ngram = NGram.from("hello");
        
        // 获取迭代器
        Iterator<IndexedCharacter> iterator = ngram.iterator();
        
        // 验证hasNext方法
        assertTrue("Iterator should have elements", iterator.hasNext());
        
        // 验证next方法返回正确的字符
        IndexedCharacter first = iterator.next();
        assertEquals("First character should be 'h'", Character.valueOf('h'), first.character());
        assertEquals("First index should be 0", 0, first.index());
        
        IndexedCharacter second = iterator.next();
        assertEquals("Second character should be 'e'", Character.valueOf('e'), second.character());
        assertEquals("Second index should be 1", 1, second.index());
        
        IndexedCharacter third = iterator.next();
        assertEquals("Third character should be 'l'", Character.valueOf('l'), third.character());
        assertEquals("Third index should be 2", 2, third.index());
        
        IndexedCharacter fourth = iterator.next();
        assertEquals("Fourth character should be 'l'", Character.valueOf('l'), fourth.character());
        assertEquals("Fourth index should be 3", 3, fourth.index());
        
        IndexedCharacter fifth = iterator.next();
        assertEquals("Fifth character should be 'o'", Character.valueOf('o'), fifth.character());
        assertEquals("Fifth index should be 4", 4, fifth.index());
        
        // 验证迭代完成后hasNext返回false
        assertFalse("Iterator should be exhausted", iterator.hasNext());
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testIteratorExhausted() {
        NGram ngram = NGram.from("hi");
        
        Iterator<IndexedCharacter> iterator = ngram.iterator();
        
        // 消耗所有元素
        iterator.next(); // 'h' at index 0
        iterator.next(); // 'i' at index 1
        
        // 应该抛出NoSuchElementException
        iterator.next();
    }
    
    @Test
    public void testEmptyNGramIterator() {
        NGram emptyNGram = NGram.from("");
        
        Iterator<IndexedCharacter> iterator = emptyNGram.iterator();
        
        assertFalse("Empty NGram iterator should have no elements", iterator.hasNext());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveNotSupported() {
        NGram ngram = NGram.from("test");
        
        Iterator<IndexedCharacter> iterator = ngram.iterator();
        iterator.next(); // 获取第一个元素
        
        // remove方法应该抛出UnsupportedOperationException
        iterator.remove();
    }
} 