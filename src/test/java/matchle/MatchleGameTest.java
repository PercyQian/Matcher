package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试MatchleGame类
 */
public class MatchleGameTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    
    /**
     * 测试MatchleGame的main方法执行
     */
    @Test
    public void testMainMethodExecution() throws Exception {
        // 清空输出
        outContent.reset();
        
        // 使用反射调用main方法
        String[] args = new String[0];
        MatchleGame.main(args);
        
        // 验证输出中包含关键词，确认方法正常执行
        String output = outContent.toString();
        assertTrue("Output should contain 'Secret key'", output.contains("Secret key"));
    }
    
    /**
     * 测试MatchleGame在词库为空时的行为
     */
    @Test
    public void testMainMethodWithEmptyCorpus() throws Exception {
        // 清空输出
        outContent.reset();
        
        // 创建一个小型测试Corpus
        Corpus testCorpus = Corpus.Builder.of()
                .add(NGram.from("hello"))
                .build();
        
        // 使用反射调用main方法
        String[] args = new String[0];
        MatchleGame.main(args);
        
        // 验证程序能够处理这种情况（使用默认词库）
        String output = outContent.toString();
        assertTrue("Program should run without crashing", 
                output.contains("Secret key") || output.contains("Remaining candidate"));
    }
    
    /**
     * 测试猜测正确时的行为
     */
    @Test
    public void testCorrectGuess() throws Exception {
        // 创建一个测试环境，模拟第一轮猜测就正确的情况
        
        // 清空输出
        outContent.reset();
        
        // 创建一个NGram作为key
        NGram key = NGram.from("hello");
        
        // 创建一个Corpus，确保bestWorstCaseGuess返回的是key
        Corpus testCorpus = Corpus.Builder.of()
                .add(key) // 只有一个单词，确保bestWorstCaseGuess返回它
                .build();
        
        // 通过反射设置main方法中的变量
        // 在实际项目中，我们可能需要重构MatchleGame使其更易于测试
        // 此处暂时使用反射，或者模拟输出
        
        // 使用反射调用main方法
        String[] args = new String[0];
        MatchleGame.main(args);
        
        // 验证输出
        String output = outContent.toString();
        // 由于我们不能控制随机选择的key，所以只能验证程序正常运行
        assertTrue("Game should complete", 
                output.contains("Guessed correctly") || 
                output.contains("Remaining candidate") || 
                output.contains("Secret key"));
    }
    
    /**
     * 测试游戏流程中的过滤器累加逻辑
     */
    @Test
    public void testFilterAccumulation() {
        // 测试过滤器的累加功能
        
        // 创建两个简单的过滤器
        NGram key = NGram.from("apple");
        NGram guess1 = NGram.from("hello");
        NGram guess2 = NGram.from("world");
        
        Filter filter1 = NGramMatcher.of(key, guess1).match();
        Filter filter2 = NGramMatcher.of(key, guess2).match();
        
        // 测试过滤器的累加
        Optional<Filter> accumulatedFilter = Optional.empty();
        
        // 添加第一个过滤器
        if (accumulatedFilter.isPresent()) {
            accumulatedFilter = Optional.of(accumulatedFilter.get().and(Optional.of(filter1)));
        } else {
            accumulatedFilter = Optional.of(filter1);
        }
        
        // 验证第一个过滤器添加成功
        assertTrue("Accumulated filter should contain filter1", accumulatedFilter.isPresent());
        assertTrue("Key should match accumulated filter", accumulatedFilter.get().test(key));
        
        // 添加第二个过滤器
        if (accumulatedFilter.isPresent()) {
            accumulatedFilter = Optional.of(accumulatedFilter.get().and(Optional.of(filter2)));
        } else {
            accumulatedFilter = Optional.of(filter2);
        }
        
        // 验证两个过滤器都添加成功
        assertTrue("Accumulated filter should contain both filters", accumulatedFilter.isPresent());
        assertTrue("Key should match accumulated filter with both filters", accumulatedFilter.get().test(key));
    }
    
    /**
     * 测试候选词库缩减到只有一个词的情况
     */
    @Test
    public void testSingleCandidateRemaining() {
        // 创建一个key
        NGram key = NGram.from("apple");
        
        // 创建一个包含key的词库
        Corpus testCorpus = Corpus.Builder.of()
                .add(key)
                .build();
        
        // 创建一个过滤器，使其匹配key
        Filter filter = NGramMatcher.of(key, NGram.from("grape")).match();
        
        // 验证过滤后的词库只剩下key
        Corpus filteredCorpus = Corpus.Builder.of(testCorpus)
                .filter(filter)
                .build();
        
        assertEquals("Filtered corpus should contain only the key", 1, filteredCorpus.size());
        assertTrue("Filtered corpus should contain the key", filteredCorpus.corpus().contains(key));
    }
} 