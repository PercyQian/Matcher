package matchle.scoring;

import matchle.Corpus;
import matchle.NGram;

/**
 * 评分策略接口，使用策略模式减少代码重复
 */
public interface ScoringStrategy {
    /**
     * 计算给定猜测的分数
     */
    double calculateScore(Corpus corpus, NGram guess);
    
    /**
     * 找出最佳猜测
     */
    NGram findBestGuess(Corpus corpus);
} 