package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.lang.reflect.Method;

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
    
    // 测试main方法（仅调用，不检查结果）
    @Test
    public void testMainMethodExecution() throws Exception {
        // 使用反射调用main方法，避免在测试中显示输出
        try {
            Method mainMethod = CorpusLoader.class.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]);
            // 如果没有异常发生，测试通过
            assertTrue(true);
        } catch (Exception e) {
            fail("Main method execution failed: " + e.getMessage());
        }
    }
    
    // 测试testHardCase方法
    @Test
    public void testHardCaseExecution() throws Exception {
        try {
            Method testHardCaseMethod = CorpusLoader.class.getMethod("testHardCase");
            testHardCaseMethod.invoke(null);
            // 如果没有异常发生，测试通过
            assertTrue(true);
        } catch (Exception e) {
            fail("testHardCase method execution failed: " + e.getMessage());
        }
    }
} 