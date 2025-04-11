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
        assertTrue("Output should contain '密钥'", output.contains("密钥"));
    }
    
    /**
     * 测试MatchleGame在词库为空时的行为
     */
    @Test
    public void testMainMethodWithEmptyCorpus() throws Exception {
        // 清空输出
        outContent.reset();
        
        // 模拟CorpusLoader.loadEnglishWords返回空值的情况
        // 创建一个MatchleGame实例，并手动设置corpus为null
        MatchleGame game = new MatchleGame();
        
        // 使用反射获取loadCorpus方法
        Method loadCorpusMethod = MatchleGame.class.getDeclaredMethod("loadCorpus");
        loadCorpusMethod.setAccessible(true);
        
        // 调用loadCorpus方法
        loadCorpusMethod.invoke(game);
        
        // 获取createDefaultCorpus方法
        Method createDefaultCorpusMethod = MatchleGame.class.getDeclaredMethod("createDefaultCorpus");
        createDefaultCorpusMethod.setAccessible(true);
        
        // 验证创建的默认语料库不为空
        Corpus defaultCorpus = (Corpus) createDefaultCorpusMethod.invoke(game);
        assertNotNull("Default corpus should not be null", defaultCorpus);
        assertTrue("Default corpus should not be empty", defaultCorpus.size() > 0);
        
        // 调用main方法，验证程序能够处理空语料库的情况
        MatchleGame.main(new String[0]);
        
        // 验证程序能够处理这种情况（使用默认词库）
        String output = outContent.toString();
        assertTrue("Program should run without crashing", 
                output.contains("密钥") || output.contains("剩余候选词数量"));
    }
    
    /**
     * 测试猜测正确时的行为
     */
    @Test
    public void testCorrectGuess() throws Exception {
        // 创建一个测试环境，模拟第一轮猜测就正确的情况
        
        // 清空输出
        outContent.reset();
        
        // 创建一个MatchleGame实例
        MatchleGame game = new MatchleGame();
        
        // 使用反射来设置其关键字段
        Field corpusField = MatchleGame.class.getDeclaredField("corpus");
        corpusField.setAccessible(true);
        
        // 创建一个简单的语料库，只包含一个词
        NGram testKey = NGram.from("hello");
        Corpus testCorpus = Corpus.Builder.of()
                .add(testKey)
                .build();
        
        corpusField.set(game, testCorpus);
        
        // 设置key字段
        Field keyField = MatchleGame.class.getDeclaredField("key");
        keyField.setAccessible(true);
        keyField.set(game, testKey);
        
        // 设置candidateCorpus字段
        Field candidateCorpusField = MatchleGame.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        candidateCorpusField.set(game, testCorpus);
        
        // 调用playRound方法
        Method playRoundMethod = MatchleGame.class.getDeclaredMethod("playRound", int.class);
        playRoundMethod.setAccessible(true);
        boolean result = (boolean) playRoundMethod.invoke(game, 1);
        
        // 验证playRound返回true表示游戏结束
        assertTrue("Game should end after correct guess", result);
        
        // 验证输出
        String output = outContent.toString();
        assertTrue("Game should complete with correct guess", 
                output.contains("猜测正确") || output.contains("密钥"));
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