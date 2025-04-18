package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public class CorpusLoaderTest {
    
    @Test
    public void testLoadEnglishWords() {
        // 由于loadEnglishWords会从网络下载词库，我们可以测试结果是否合理
        // 注意：此测试依赖于网络连接
        Corpus corpus = CorpusLoader.loadEnglishWords(5);
        
        // 验证返回的词库不为null且包含单词
        assertNotNull("Corpus should not be null", corpus);
        assertTrue("Corpus should contain words", corpus.size() > 0);
        
        // 验证所有单词长度为5
        for (NGram word : corpus.corpus()) {
            assertEquals("All words should be 5 characters long", 5, word.size());
        }
    }
    
    @Test
    public void testLoadEnglishWordsWithDifferentLength() {
        // 测试加载4字母单词
        Corpus corpus4 = CorpusLoader.loadEnglishWords(4);
        assertNotNull("Corpus should not be null", corpus4);
        
        if (corpus4.size() > 0) {
            // 验证所有单词长度为4
            for (NGram word : corpus4.corpus()) {
                assertEquals("All words should be 4 characters long", 4, word.size());
            }
        }
    }
} 