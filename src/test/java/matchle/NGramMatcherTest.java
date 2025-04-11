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

        // 'e' 在 "apple" 和 "hello" 中都有，但位置不同，应建立 containsElsewhere(4, 'e') 的过滤条件
        Filter filter = NGramMatcher.of(key, guess).match();
        assertFalse("hello should not match", filter.test(NGram.from("hello")));
        assertTrue("apple should match", filter.test(NGram.from("apple")));
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

        assertEquals("Only 'apple' should match", 1, corpus.size(filter));
    }

    /**
     * 测试较复杂的过滤条件。
     * 对于 key "redux" 和 guess "hello"：
     * - "e" 在两者中位置不同 -> containsElsewhere(1, 'e')
     * - "hello" 中没有 'r' -> notContains('r')
     * - "hello" 中没有 'd' -> notContains('d')
     * - "hello" 中没有 'u' -> notContains('u')
     * - "hello" 中没有 'x' -> notContains('x')
     */
    @Test
    public void testComplexCriteria() {
        Filter filter = NGramMatcher.of(NGram.from("redux"), NGram.from("hello")).match();
        assertTrue("redux should match", filter.test(NGram.from("redux")));
    }
}