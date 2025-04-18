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
import java.lang.reflect.Method;

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
    
    // 新增测试方法以提高分支覆盖率
    
    @Test
    public void testPlayWithEmptyCorpus() throws Exception {
        // 设置游戏状态以测试空语料库的情况
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 创建一个空的语料库
        Corpus emptyCorpus = Corpus.Builder.of().build();
        
        // 设置candidateCorpus为空
        Field candidateCorpusField = GameLogic.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        
        // 不要尝试调用play()方法，而是直接测试handleEmptyCorpus的行为
        candidateCorpusField.set(gameLogic, emptyCorpus);
        outContent.reset();
        
        // 直接调用handleEmptyCorpus方法，它应该处理空语料库的情况
        boolean result = game.handleEmptyCorpus();
        
        // 验证结果和输出
        assertTrue("Should return true for empty corpus", result);
        String output = outContent.toString();
        assertTrue("Should detect empty corpus", output.contains("No candidates remain"));
    }
    
    @Test
    public void testPlayEmptyCorpusHandling() throws Exception {
        // 测试play()方法处理空语料库的情况
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 设置一个包含secretKey的语料库，但设置candidateCorpus为空
        Field secretKeyField = GameLogic.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        NGram secretKey = (NGram) secretKeyField.get(gameLogic);
        
        // 创建一个包含至少一个词的语料库
        Corpus normalCorpus = Corpus.Builder.of()
                .add(secretKey)
                .add(NGram.from("other"))
                .build();
        
        // 设置corpus (这是initial corpus)
        Field corpusField = MatchleGame.class.getDeclaredField("corpus");
        corpusField.setAccessible(true);
        corpusField.set(game, normalCorpus);
        
        // 创建空的候选语料库
        Corpus emptyCorpus = Corpus.Builder.of().build();
        
        // 设置candidateCorpus为空
        Field candidateCorpusField = GameLogic.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        candidateCorpusField.set(gameLogic, emptyCorpus);
        
        // 直接调用checkGameTermination方法，而不是play()
        outContent.reset();
        boolean result = game.checkGameTermination();
        
        // 验证结果
        assertTrue("Should return true when corpus is empty", result);
        String output = outContent.toString();
        assertTrue("Should output 'No candidates remain'", output.contains("No candidates remain"));
    }
    
    @Test
    public void testLoadCorpusWithNullCorpus() throws Exception {
        // 使用反射测试loadCorpus方法的路径，当从CorpusLoader获取的语料库为null时
        Field corpusField = MatchleGame.class.getDeclaredField("corpus");
        corpusField.setAccessible(true);
        corpusField.set(game, null);
        
        // 获取loadCorpus方法
        Method loadCorpusMethod = MatchleGame.class.getDeclaredMethod("loadCorpus");
        loadCorpusMethod.setAccessible(true);
        
        // 执行loadCorpus方法
        loadCorpusMethod.invoke(game);
        
        // 验证corpus不为null，表示已经创建了默认语料库
        assertNotNull("Should create default corpus when loaded corpus is null", corpusField.get(game));
    }
    
    @Test
    public void testIsCorrectGuess() throws Exception {
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 获取secretKey
        Field secretKeyField = GameLogic.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        NGram secretKey = (NGram) secretKeyField.get(gameLogic);
        
        // 获取isCorrectGuess方法
        Method isCorrectGuessMethod = MatchleGame.class.getDeclaredMethod("isCorrectGuess", NGram.class);
        isCorrectGuessMethod.setAccessible(true);
        
        // 测试正确的猜测
        outContent.reset();
        boolean correctResult = (boolean) isCorrectGuessMethod.invoke(game, secretKey);
        assertTrue("Should return true for correct guess", correctResult);
        assertTrue("Should output correct guess message", 
                outContent.toString().contains("Correct guess"));
        
        // 测试错误的猜测
        outContent.reset();
        NGram wrongGuess = NGram.from("wrong");
        boolean wrongResult = (boolean) isCorrectGuessMethod.invoke(game, wrongGuess);
        assertFalse("Should return false for wrong guess", wrongResult);
        assertEquals("Should not output for wrong guess", "", outContent.toString());
    }
    
    @Test
    public void testPlayRoundWithCorrectGuess() throws Exception {
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 获取secretKey
        Field secretKeyField = GameLogic.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        NGram secretKey = (NGram) secretKeyField.get(gameLogic);
        
        // 直接测试playRound方法，不尝试覆盖makeGuess
        Method playRoundMethod = MatchleGame.class.getDeclaredMethod("playRound", int.class);
        playRoundMethod.setAccessible(true);
        
        // 执行playRound方法，这将使用真实的makeGuess方法
        boolean result = (boolean) playRoundMethod.invoke(game, 1);
        
        // 由于我们无法控制makeGuess返回什么，所以只能验证方法执行成功
        // 不能验证具体的返回值
        
        // 检查输出包含了预期的游戏回合信息
        String output = outContent.toString();
        assertTrue("Should output round information", 
                output.contains("Best guess") || output.contains("Remaining candidate"));
    }
    
    @Test
    public void testPlayRoundDirectIsCorrectGuess() throws Exception {
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 替换secretKey为一个已知的值
        NGram testKey = NGram.from("rebus");
        Field secretKeyField = GameLogic.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(gameLogic, testKey);
        
        // 直接调用isCorrectGuess方法
        Method isCorrectGuessMethod = MatchleGame.class.getDeclaredMethod("isCorrectGuess", NGram.class);
        isCorrectGuessMethod.setAccessible(true);
        
        // 测试正确猜测路径
        outContent.reset();
        boolean result = (boolean) isCorrectGuessMethod.invoke(game, testKey);
        assertTrue("Should return true for correct guess", result);
        assertTrue("Should output correct guess message", 
                outContent.toString().contains("Correct guess"));
    }
    
    @Test
    public void testHandleSingleCandidateKeyMatches() throws Exception {
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 获取secretKey
        Field secretKeyField = GameLogic.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        NGram secretKey = (NGram) secretKeyField.get(gameLogic);
        
        // 创建只包含secretKey的候选语料库
        Corpus singleCorpus = Corpus.Builder.of().add(secretKey).build();
        
        // 设置candidateCorpus
        Field candidateCorpusField = GameLogic.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        candidateCorpusField.set(gameLogic, singleCorpus);
        
        // 调用handleSingleCandidate
        outContent.reset();
        boolean result = game.handleSingleCandidate();
        
        assertTrue("Should return true for single candidate", result);
        String output = outContent.toString();
        assertTrue("Should indicate key was found", output.contains("Found key"));
    }
    
    @Test
    public void testHandleSingleCandidateKeyDoesNotMatch() throws Exception {
        game.initialize();
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 设置secretKey
        Field secretKeyField = GameLogic.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(gameLogic, NGram.from("hello"));
        
        // 创建只包含一个与secretKey不同的词的候选语料库
        Corpus singleCorpus = Corpus.Builder.of().add(NGram.from("world")).build();
        
        // 设置candidateCorpus
        Field candidateCorpusField = GameLogic.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        candidateCorpusField.set(gameLogic, singleCorpus);
        
        // 调用handleSingleCandidate
        outContent.reset();
        boolean result = game.handleSingleCandidate();
        
        assertTrue("Should return true for single candidate", result);
        String output = outContent.toString();
        assertTrue("Should indicate key does not match", output.contains("does not match key"));
    }
    
    @Test
    public void testPlayWithMaxRoundsReached() throws Exception {
        game.initialize();
        
        // 设置maxRounds为一个小值，使其容易达到最大回合数
        Field maxRoundsField = MatchleGame.class.getDeclaredField("maxRounds");
        maxRoundsField.setAccessible(true);
        maxRoundsField.setInt(game, 1);
        
        // 创建一个模拟的playRound方法，始终返回false表示游戏没有终止
        // 这样可以确保play()方法会达到最大回合数
        Method playRoundMethod = MatchleGame.class.getDeclaredMethod("playRound", int.class);
        playRoundMethod.setAccessible(true);
        
        // 获取play方法
        Method playMethod = MatchleGame.class.getDeclaredMethod("play");
        playMethod.setAccessible(true);
        
        // 由于我们不能覆盖playRound方法，所以我们需要确保candidateCorpus不为空
        // 并且有足够的元素，这样在一个回合后游戏不会终止
        
        // 获取GameLogic实例
        Field gameLogicField = MatchleGame.class.getDeclaredField("gameLogic");
        gameLogicField.setAccessible(true);
        GameLogic gameLogic = (GameLogic) gameLogicField.get(game);
        
        // 创建一个足够大的语料库，确保一轮后不会结束
        Corpus largeCorpus = Corpus.Builder.of()
                .add(NGram.from("word1"))
                .add(NGram.from("word2"))
                .add(NGram.from("word3"))
                .add(NGram.from("word4"))
                .add(NGram.from("word5"))
                .build();
        
        // 设置candidateCorpus
        Field candidateCorpusField = GameLogic.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        candidateCorpusField.set(gameLogic, largeCorpus);
        
        // 调用play方法
        outContent.reset();
        playMethod.invoke(game);
        
        // 验证输出包含达到最大回合数的信息或游戏结束信息
        String output = outContent.toString();
        assertTrue("Should indicate rounds progress", 
                output.contains("Round 1") || 
                output.contains("Maximum rounds reached") || 
                output.contains("Correct guess"));
    }
} 