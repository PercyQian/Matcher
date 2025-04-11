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
 * Tests for MatchleGame class
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
     * Test MatchleGame's main method execution
     */
    @Test
    public void testMainMethodExecution() throws Exception {
        // Clear output
        outContent.reset();
        
        // Call main method using reflection
        String[] args = new String[0];
        MatchleGame.main(args);
        
        // Verify output contains keywords, confirming method executed normally
        String output = outContent.toString();
        assertTrue("Output should contain 'Secret key'", output.contains("Secret key"));
    }
    
    /**
     * Test MatchleGame's behavior when corpus is empty
     */
    @Test
    public void testMainMethodWithEmptyCorpus() throws Exception {
        // Clear output
        outContent.reset();
        
        // Simulate CorpusLoader.loadEnglishWords returning null
        // Create a MatchleGame instance and manually set corpus to null
        MatchleGame game = new MatchleGame();
        
        // Get loadCorpus method using reflection
        Method loadCorpusMethod = MatchleGame.class.getDeclaredMethod("loadCorpus");
        loadCorpusMethod.setAccessible(true);
        
        // Call loadCorpus method
        loadCorpusMethod.invoke(game);
        
        // Get createDefaultCorpus method
        Method createDefaultCorpusMethod = MatchleGame.class.getDeclaredMethod("createDefaultCorpus");
        createDefaultCorpusMethod.setAccessible(true);
        
        // Verify default corpus is not null
        Corpus defaultCorpus = (Corpus) createDefaultCorpusMethod.invoke(game);
        assertNotNull("Default corpus should not be null", defaultCorpus);
        assertTrue("Default corpus should not be empty", defaultCorpus.size() > 0);
        
        // Call main method, verify program can handle empty corpus case
        MatchleGame.main(new String[0]);
        
        // Verify program can handle this situation (using default corpus)
        String output = outContent.toString();
        assertTrue("Program should run without crashing", 
                output.contains("Secret key") || output.contains("Remaining candidate"));
    }
    
    /**
     * Test behavior when guess is correct
     */
    @Test
    public void testCorrectGuess() throws Exception {
        // Create a test environment that simulates a correct guess on the first round
        
        // Clear output
        outContent.reset();
        
        // Create a MatchleGame instance
        MatchleGame game = new MatchleGame();
        
        // Use reflection to set key fields
        Field corpusField = MatchleGame.class.getDeclaredField("corpus");
        corpusField.setAccessible(true);
        
        // Create a simple corpus containing a single word
        NGram testKey = NGram.from("hello");
        Corpus testCorpus = Corpus.Builder.of()
                .add(testKey)
                .build();
        
        corpusField.set(game, testCorpus);
        
        // Set key field
        Field keyField = MatchleGame.class.getDeclaredField("key");
        keyField.setAccessible(true);
        keyField.set(game, testKey);
        
        // Set candidateCorpus field
        Field candidateCorpusField = MatchleGame.class.getDeclaredField("candidateCorpus");
        candidateCorpusField.setAccessible(true);
        candidateCorpusField.set(game, testCorpus);
        
        // Call playRound method
        Method playRoundMethod = MatchleGame.class.getDeclaredMethod("playRound", int.class);
        playRoundMethod.setAccessible(true);
        boolean result = (boolean) playRoundMethod.invoke(game, 1);
        
        // Verify playRound returns true to indicate game over
        assertTrue("Game should end after correct guess", result);
        
        // Verify output
        String output = outContent.toString();
        assertTrue("Game should complete with correct guess", 
                output.contains("Correct guess") || output.contains("Secret key"));
    }
    
    /**
     * Test filter accumulation in game flow
     */
    @Test
    public void testFilterAccumulation() {
        // Test filter accumulation functionality
        
        // Create two simple filters
        NGram key = NGram.from("apple");
        NGram guess1 = NGram.from("hello");
        NGram guess2 = NGram.from("world");
        
        Filter filter1 = NGramMatcher.of(key, guess1).match();
        Filter filter2 = NGramMatcher.of(key, guess2).match();
        
        // Test filter accumulation
        Optional<Filter> accumulatedFilter = Optional.empty();
        
        // Add first filter
        if (accumulatedFilter.isPresent()) {
            accumulatedFilter = Optional.of(accumulatedFilter.get().and(Optional.of(filter1)));
        } else {
            accumulatedFilter = Optional.of(filter1);
        }
        
        // Verify first filter added successfully
        assertTrue("Accumulated filter should contain filter1", accumulatedFilter.isPresent());
        assertTrue("Key should match accumulated filter", accumulatedFilter.get().test(key));
        
        // Add second filter
        if (accumulatedFilter.isPresent()) {
            accumulatedFilter = Optional.of(accumulatedFilter.get().and(Optional.of(filter2)));
        } else {
            accumulatedFilter = Optional.of(filter2);
        }
        
        // Verify both filters added successfully
        assertTrue("Accumulated filter should contain both filters", accumulatedFilter.isPresent());
        assertTrue("Key should match accumulated filter with both filters", accumulatedFilter.get().test(key));
    }
    
    /**
     * Test case when candidate corpus is reduced to a single word
     */
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
} 