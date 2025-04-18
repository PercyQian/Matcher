package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.List;

public class GameLogicTest {
    
    private GameLogic gameLogic;
    private Corpus testCorpus;
    private NGram testKey;
    
    @Before
    public void setUp() {
        gameLogic = new GameLogic();
        testCorpus = Corpus.Builder.of()
                .add(NGram.from("hello"))
                .add(NGram.from("world"))
                .add(NGram.from("apple"))
                .build();
        testKey = NGram.from("hello");
    }
    
    @Test
    public void testCreateDefaultCorpus() {
        Corpus defaultCorpus = gameLogic.createDefaultCorpus();
        assertNotNull("Default corpus should not be null", defaultCorpus);
        assertTrue("Default corpus should contain 'rebus'", 
                defaultCorpus.corpus().contains(NGram.from("rebus")));
        assertTrue("Default corpus should contain 'redux'", 
                defaultCorpus.corpus().contains(NGram.from("redux")));
    }
    
    @Test
    public void testInitializeWithKey() {
        gameLogic.initialize(testCorpus, testKey);
        assertEquals("Secret key should match", testKey, gameLogic.getSecretKey());
        assertEquals("Corpus should match", testCorpus, gameLogic.getCandidateCorpus());
    }
    
    @Test
    public void testInitializeWithoutKey() {
        gameLogic.initialize(testCorpus);
        assertNotNull("Secret key should be set", gameLogic.getSecretKey());
        assertTrue("Secret key should be in corpus", 
                testCorpus.corpus().contains(gameLogic.getSecretKey()));
    }
    
    @Test
    public void testProcessGuess() {
        gameLogic.initialize(testCorpus, testKey);
        NGram guess = NGram.from("world");
        Filter filter = gameLogic.processGuess(guess);
        assertNotNull("Filter should not be null", filter);
        assertTrue("Filter should test against key", filter.test(testKey));
    }
    
    @Test
    public void testUpdateAccumulatedFilter() {
        gameLogic.initialize(testCorpus, testKey);
        NGram guess1 = NGram.from("world");
        NGram guess2 = NGram.from("apple");
        
        Filter filter1 = gameLogic.processGuess(guess1);
        Filter filter2 = gameLogic.processGuess(guess2);
        
        Filter accumulatedFilter = gameLogic.getAccumulatedFilter();
        assertNotNull("Accumulated filter should not be null", accumulatedFilter);
        assertTrue("Accumulated filter should test against key", accumulatedFilter.test(testKey));
    }
    
    @Test
    public void testUpdateCandidateCorpus() {
        gameLogic.initialize(testCorpus, testKey);
        NGram guess = NGram.from("world");
        gameLogic.processGuess(guess);
        
        Corpus updatedCorpus = gameLogic.getCandidateCorpus();
        assertNotNull("Updated corpus should not be null", updatedCorpus);
        assertTrue("Updated corpus should be smaller than original", 
                updatedCorpus.size() <= testCorpus.size());
    }
    
    @Test
    public void testLoadAndCreateGameState() {
        gameLogic.initialize(testCorpus, testKey);
        NGram guess = NGram.from("world");
        gameLogic.processGuess(guess);
        
        GameState state = gameLogic.createGameState();
        assertNotNull("Game state should not be null", state);
        
        GameLogic newGameLogic = new GameLogic();
        newGameLogic.loadState(state);
        
        assertEquals("Secret key should match", 
                gameLogic.getSecretKey(), newGameLogic.getSecretKey());
        assertEquals("Candidate corpus should match", 
                gameLogic.getCandidateCorpus().size(), 
                newGameLogic.getCandidateCorpus().size());
    }
    
    @Test
    public void testHasGameTerminated() {
        gameLogic.initialize(testCorpus, testKey);
        assertFalse("Game should not be terminated initially", gameLogic.hasGameTerminated());
        
        // Process guesses until only one candidate remains
        while (!gameLogic.hasGameTerminated()) {
            NGram guess = gameLogic.getBestGuess();
            gameLogic.processGuess(guess);
        }
        
        assertTrue("Game should be terminated", gameLogic.hasGameTerminated());
    }
} 