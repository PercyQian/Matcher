package matchle.scoring;

import matchle.Corpus;
import matchle.NGram;

/**
 * scoring strategy interface, use strategy pattern to reduce code duplication
 */
public interface ScoringStrategy {
    /**
     * calculate the score of the given guess
     */
    double calculateScore(Corpus corpus, NGram guess);
    
    /**
     * find the best guess
     */
    NGram findBestGuess(Corpus corpus);
} 