package matchle;

import java.util.function.ToLongFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试Corpus类的评分功能
 */
public class CorpusScoringTest {
    
    private Corpus corpus;
    
    @Before
    public void setUp() {
        // 构造包含 4 个 n-gram 的词库
        // construct corpus include 4 ngram
        Corpus.Builder builder = Corpus.Builder.of()
                    .add(NGram.from("rebus"))
                    .add(NGram.from("redux"))
                    .add(NGram.from("route"))
                    .add(NGram.from("hello"));
        
        corpus = builder.build();
        assertNotNull("Corpus should not be null when valid n-grams are added", corpus);
    }
    
    @Test
    public void testScore() {
        // 对于 key "rebus" 和 guess "route"，反馈筛选后，只有 "rebus" 和 "redux" 符合条件，score 应返回 2
        // for key 'rebus' and guess 'route' after flitering, only 'rebus' and redux fullfill condion, return 2
        long score = corpus.score(NGram.from("rebus"), NGram.from("route"));
        assertEquals("Score for key 'rebus' and guess 'route' should be 2", 2, score);
    }
    
    @Test
    public void testScoreWorstCase() {
        // 根据示例，对于 guess "route"，worst-case 得分应为 2
        // for
        long worstCase = corpus.scoreWorstCase(NGram.from("route"));
        assertEquals("Worst-case score for guess 'route' should be 2", 2, worstCase);
    }
    
    @Test
    public void testScoreAverageCase() {
        // 根据示例，对于 guess "route"，average-case 得分应为 (2+1+1+1)/4 = 1.5
        double avgCase = corpus.scoreAverageCase(NGram.from("route"));
        assertEquals("Average-case score for guess 'route' should be 1.5", 1.5, avgCase, 0.0001);
    }
    
    @Test
    public void testBestWorstCaseGuess() {
        NGram bestWorst = corpus.bestWorstCaseGuess();
        assertNotNull("Best worst-case guess should not be null", bestWorst);
        
        // 遍历 corpus，计算每个候选猜测的 worst-case 得分，并找出最小值
        long minWorst = Long.MAX_VALUE;
        for (NGram candidate : corpus) {
            long worst = corpus.scoreWorstCase(candidate);
            if (worst < minWorst) {
                minWorst = worst;
            }
        }
        long bestWorstScore = corpus.scoreWorstCase(bestWorst);
        assertEquals("Best worst-case guess should yield minimal worst-case score", minWorst, bestWorstScore);
    }
    
    @Test
    public void testBestAverageCaseGuess() {
        NGram bestAvg = corpus.bestAverageCaseGuess();
        assertNotNull("Best average-case guess should not be null", bestAvg);
        
        // 遍历 corpus，计算每个候选猜测的 average-case 得分，并找出最小值
        double minAvg = Double.MAX_VALUE;
        for (NGram candidate : corpus) {
            double avg = corpus.scoreAverageCase(candidate);
            if (avg < minAvg) {
                minAvg = avg;
            }
        }
        double bestAvgScore = corpus.scoreAverageCase(bestAvg);
        assertEquals("Best average-case guess should yield minimal average-case score", minAvg, bestAvgScore, 0.0001);
    }
    
    @Test
    public void testBestGuessWithCustomCriterion() {
        // 使用 worst-case 得分作为自定义准则
        ToLongFunction<NGram> worstCaseCriterion = ngram -> corpus.scoreWorstCase(ngram);
        NGram bestCustom = corpus.bestGuess(worstCaseCriterion);
        assertNotNull("Best guess with custom criterion should not be null", bestCustom);
        
        // bestGuess 应该与 bestWorstCaseGuess 一致（因为两者均采用 worst-case 得分作为准则）
        NGram bestWorst = corpus.bestWorstCaseGuess();
        long scoreCustom = corpus.scoreWorstCase(bestCustom);
        long scoreWorst = corpus.scoreWorstCase(bestWorst);
        assertEquals("Custom best guess should match best worst-case guess when using worst-case criterion", scoreWorst, scoreCustom);
    }
    
    @Test
    public void testEmptyCorpus() {
        // 根据设计要求，当没有添加任何 n-gram 时，build() 应返回 null
        Corpus emptyCorpus = Corpus.Builder.of().build();
        assertNull("Empty corpus should be null when no n-grams are added", emptyCorpus);
    }
}
