package matchle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * performance test for large corpus
 */
public class CorpusPerformanceTest {
    
    private Corpus largeCorpus;
    private static final int CORPUS_SIZE = 500; // large corpus size
    private static final int WORD_LENGTH = 5;
    
    @Before
    public void setUp() {
        // generate large corpus of random words
        Random random = new Random(42); // fixed seed for reproducibility
        List<Character> chars = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
        
        Corpus.Builder builder = Corpus.Builder.of();
        for (int i = 0; i < CORPUS_SIZE; i++) {
            List<Character> word = new ArrayList<>();
            for (int j = 0; j < WORD_LENGTH; j++) {
                word.add(chars.get(random.nextInt(chars.size())));
            }
            builder.add(NGram.from(word));
        }
        
        largeCorpus = builder.build();
        assertNotNull("Large corpus should be created successfully", largeCorpus);
    }
    
    @Test
    public void testBestWorstCaseGuessPerformance() {
        long startTime = System.nanoTime();
        
        NGram bestGuess = largeCorpus.bestWorstCaseGuess();
        
        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        // record execution time for performance analysis
        System.out.println("bestWorstCaseGuess took " + durationMs + " ms for corpus size " + CORPUS_SIZE);
        
        assertNotNull("Best worst-case guess should be found", bestGuess);
        // in actual project, we may need to set a reasonable time limit
        assertTrue("bestWorstCaseGuess should complete within 30 seconds", durationMs < 30000);
    }
    
    @Test
    public void testMemoryUsage() {
        // record memory usage before execution
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // execute multiple scoring operations
        List<NGram> ngrams = new ArrayList<>(largeCorpus.corpus());
        NGram testGuess = ngrams.get(0);
        
        for (int i = 0; i < 100; i++) {
            largeCorpus.scoreWorstCase(testGuess);
        }
        
        // record memory usage after execution
        runtime.gc();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        
        // calculate memory growth
        long memoryGrowth = usedMemoryAfter - usedMemoryBefore;
        System.out.println("Memory growth after 100 scoreWorstCase calls: " + memoryGrowth + " bytes");
        
        // ensure memory growth is within a reasonable range
        assertTrue("Memory growth should be less than 100MB", memoryGrowth < 100 * 1024 * 1024);
    }
} 