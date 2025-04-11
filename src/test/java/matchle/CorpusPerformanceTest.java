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
 * 性能压力测试，测试大规模词库下的算法表现
 */
public class CorpusPerformanceTest {
    
    private Corpus largeCorpus;
    private static final int CORPUS_SIZE = 500; // 更大的测试可以设置更高的值
    private static final int WORD_LENGTH = 5;
    
    @Before
    public void setUp() {
        // 生成大量随机单词作为测试词库
        Random random = new Random(42); // 固定种子以确保测试可重复
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
        
        // 记录执行时间，以便进行性能分析
        System.out.println("bestWorstCaseGuess took " + durationMs + " ms for corpus size " + CORPUS_SIZE);
        
        assertNotNull("Best worst-case guess should be found", bestGuess);
        // 实际项目中可能需要设置一个合理的时间上限
        assertTrue("bestWorstCaseGuess should complete within 30 seconds", durationMs < 30000);
    }
    
    @Test
    public void testMemoryUsage() {
        // 记录执行前的内存使用
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // 执行多次评分操作
        List<NGram> ngrams = new ArrayList<>(largeCorpus.corpus());
        NGram testGuess = ngrams.get(0);
        
        for (int i = 0; i < 100; i++) {
            largeCorpus.scoreWorstCase(testGuess);
        }
        
        // 记录执行后的内存使用
        runtime.gc();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        
        // 计算内存增长
        long memoryGrowth = usedMemoryAfter - usedMemoryBefore;
        System.out.println("Memory growth after 100 scoreWorstCase calls: " + memoryGrowth + " bytes");
        
        // 确保内存增长在合理范围内
        assertTrue("Memory growth should be less than 100MB", memoryGrowth < 100 * 1024 * 1024);
    }
} 