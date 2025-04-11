package matchle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.List;
import java.util.function.Predicate;

public class NGramMatcherTest {

    @Test
    public void testExactMatch() {
        NGram key = NGram.from("apple");
        NGram guess = NGram.from("apple");

        Filter filter = NGramMatcher.of(key, guess).match();
        assertTrue("The key should match itself", filter.test(NGram.from("apple")));
    }

    @Test
    public void testSimpleCriteria() {
        NGram key = NGram.from("apple");
        NGram guess = NGram.from("hello");

        // 'e' 在 "apple" 和 "hello" 中都有，但位置不同
        Filter filter = NGramMatcher.of(key, guess).match();
        
        // 修正:在这里，"hello"不应该匹配模式，因为它已经是用来生成过滤器的猜测
        assertFalse("hello should not match", filter.test(NGram.from("hello")));
        
        // "apple"是密钥，应该匹配
        assertTrue("apple should match", filter.test(NGram.from("apple")));
        
        // 测试其他不应匹配的词
        assertFalse("ppale should not match", filter.test(NGram.from("ppale")));
    }

    @Test
    public void testCorpusFiltering() {
        NGram key = NGram.from("apple");
        NGram guess = NGram.from("hello");

        Filter filter = NGramMatcher.of(key, guess).match();

        // 构建包含多个单词的词库
        Corpus corpus = Corpus.Builder.of()
                .add(NGram.from("axxxx"))  // 不匹配
                .add(NGram.from("appxx"))  // 不匹配
                .add(NGram.from("apple"))  // 匹配
                .build();

        // 对于"apple"作为密钥，"hello"作为猜测生成的过滤器，
        // 只有"apple"应该通过过滤器
        assertEquals("Only 'apple' should match", 1, corpus.size(filter));
    }

    /**
     * 测试较复杂的过滤条件。
     * 对于 key "redux" 和 guess "hello"：
     * - "e" 在两者中位置不同
     * - "hello" 中没有 'r', 'd', 'u', 'x'
     */
    @Test
    public void testComplexCriteria() {
        NGram key = NGram.from("redux");
        NGram guess = NGram.from("hello");
        
        // 生成过滤条件
        Filter filter = NGramMatcher.of(key, guess).match();
        
        // 密钥本身应该匹配过滤条件
        assertTrue("redux should match", filter.test(key));
        
        // "hello"不应该匹配，因为它是用来生成过滤条件的猜测
        assertFalse("hello should not match", filter.test(guess));
        
        // 创建一些测试用例
        assertTrue("reduX should match", filter.test(NGram.from("redux")));
        assertFalse("hello should not match", filter.test(NGram.from("hello")));
        
        // 测试更多例子，确保过滤条件正确工作
        assertFalse("rebus should not match", filter.test(NGram.from("rebus"))); // 没有'd'和'x'，但有'e'
        assertFalse("linux should not match", filter.test(NGram.from("linux"))); // 有'x'但没有'r'，'e'位置错误
    }
}