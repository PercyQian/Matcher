package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;

/**
 * Tests for MatchleGame class
 */
public class MatchleGameTest {
    
    private MatchleGame game;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @Before
    public void setUp() {
        game = new MatchleGame();
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    
    @Test
    public void testGameInitialization() {
        assertNotNull("Game should be initialized", game);
    }
    
    @Test
    public void testGamePlayWithDefaultCorpus() {
        game.playGame(); // 新增的公共方法
        
        String output = outContent.toString();
        assertTrue("Game should output secret key", 
                output.contains("Secret key") || output.contains("Remaining candidate"));
    }
    
    @Test
    public void testFilterAccumulation() {
        // Test filter accumulation functionality
        NGram key = NGram.from("apple");
        NGram guess1 = NGram.from("hello");
        NGram guess2 = NGram.from("world");
        
        Filter filter1 = NGramMatcher.of(key, guess1).match();
        Filter filter2 = NGramMatcher.of(key, guess2).match();
        
        // Test filter accumulation
        Set<Filter> accumulatedFilters = new HashSet<>();
        
        // Add first filter
        accumulatedFilters.add(filter1);
        
        // Verify first filter added successfully
        assertTrue("Accumulated filter should contain filter1", accumulatedFilters.contains(filter1));
        
        // Add second filter
        accumulatedFilters.add(filter2);
        
        // Verify both filters added successfully
        assertTrue("Accumulated filter should contain both filters", accumulatedFilters.contains(filter2));
    }
    
    @Test
    public void testSingleCandidateRemaining() {
        // Create a key
        NGram key = NGram.from("apple");
        
        // Create a corpus containing the key
        Corpus testCorpus = Corpus.Builder.of()
                .add(key)
                .build();
        
        // Create a filter that matches the key
        Filter filter = NGramMatcher.of(key, NGram.from("grape")).match();
        
        // Verify filtered corpus only contains the key
        Corpus filteredCorpus = Corpus.Builder.of(testCorpus)
                .filter(filter)
                .build();
        
        assertEquals("Filtered corpus should contain only the key", 1, filteredCorpus.size());
        assertTrue("Filtered corpus should contain the key", filteredCorpus.corpus().contains(key));
    }
    
    @Test
    public void testUpdateGameState() {
        game.initialize();
        NGram guess = NGram.from("rebus");
        game.updateGameState(guess);
        
        assertNotNull("Accumulated filter should not be null", game.getAccumulatedFilter());
        assertTrue("Candidate corpus should be updated", 
                game.getCandidateCorpus().size() <= game.getInitialCorpus().size());
    }
    
    @Test
    public void testCheckGameTermination() {
        game.initialize();
        assertFalse("Game should not be terminated initially", game.checkGameTermination());
        
        // 游戏终止测试需要更复杂的设置
        // 我们会在别的测试方法中间接测试
    }
    
    @Test
    public void testHandleSingleCandidate() {
        // 直接测试方法的行为，不尝试修改内部状态
        game.initialize();
        outContent.reset(); // 清除之前的输出
        
        // 调用方法并检查返回值
        boolean result = game.handleSingleCandidate();
        
        // 如果candidateCorpus的大小不是1，预期返回false
        if (game.getCandidateCorpus().size() != 1) {
            assertFalse("Should return false when corpus size is not 1", result);
        } else {
            // 如果candidateCorpus大小是1，预期返回true并有相应输出
            assertTrue("Should return true when corpus size is 1", result);
            String output = outContent.toString();
            assertTrue("Should print corpus information", 
                    output.contains("Candidate corpus reduced to one"));
        }
    }
    
    @Test
    public void testHandleEmptyCorpus() {
        // 直接测试方法的行为，不尝试修改内部状态
        game.initialize();
        outContent.reset(); // 清除之前的输出
        
        // 调用方法并检查返回值
        boolean result = game.handleEmptyCorpus();
        
        // 如果candidateCorpus的大小不是0，预期返回false
        if (game.getCandidateCorpus().size() != 0) {
            assertFalse("Should return false when corpus is not empty", result);
        } else {
            // 如果candidateCorpus为空，预期返回true并有相应输出
            assertTrue("Should return true when corpus is empty", result);
            String output = outContent.toString();
            assertTrue("Should print empty corpus message", 
                    output.contains("No candidates remain"));
        }
    }
    
    @Test
    public void testGetBestGuess() {
        game.initialize();
        NGram bestGuess = game.getBestGuess();
        assertNotNull("Best guess should not be null", bestGuess);
    }
    
    @Test
    public void testGetAccumulatedFilter() {
        game.initialize();
        
        // 初始状态下accumulatedFilter应该是null
        Filter initialFilter = game.getAccumulatedFilter();
        
        // 提交一个猜测以更新accumulatedFilter
        NGram guess = NGram.from("rebus");
        game.updateGameState(guess);
        
        Filter updatedFilter = game.getAccumulatedFilter();
        assertNotNull("Accumulated filter should be set after update", updatedFilter);
        
        // 如果初始filter不是null，检查它们是否不同
        if (initialFilter != null) {
            assertNotEquals("Filter should be updated", initialFilter, updatedFilter);
        }
    }
    
    @Test
    public void testGetCandidateCorpus() {
        game.initialize();
        assertNotNull("Candidate corpus should not be null", game.getCandidateCorpus());
        assertTrue("Candidate corpus should not be empty", game.getCandidateCorpus().size() > 0);
    }
    
    @Test
    public void testGetInitialCorpus() {
        game.initialize();
        assertNotNull("Initial corpus should not be null", game.getInitialCorpus());
        assertTrue("Initial corpus should not be empty", game.getInitialCorpus().size() > 0);
    }
    
    @Test
    public void testGetSecretKey() {
        game.initialize();
        assertNotNull("Secret key should not be null", game.getSecretKey());
        
        // 判断secretKey是否在initialCorpus中
        Corpus initialCorpus = game.getInitialCorpus();
        NGram secretKey = game.getSecretKey();
        assertTrue("Secret key should be in initial corpus", 
                initialCorpus.corpus().contains(secretKey));
    }
} 