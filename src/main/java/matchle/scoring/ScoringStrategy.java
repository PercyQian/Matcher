package matchle.scoring;

import matchle.Corpus;
import matchle.NGram;

/**
 * Strategy interface for scoring possible guesses in the Matchle game.
 * <p>
 * This interface defines the contract for different scoring algorithms that can
 * evaluate the quality of potential guesses based on the current game state.
 * It follows the Strategy design pattern to allow different scoring approaches
 * to be interchangeable, reducing code duplication and increasing flexibility.
 * <p>
 * Implementations of this interface can employ various heuristics such as:
 * <ul>
 *   <li>Information theory approaches (e.g., entropy maximization)</li>
 *   <li>Character frequency analysis</li>
 *   <li>Pattern matching probability</li>
 *   <li>Minimax strategies for worst-case minimization</li>
 * </ul>
 */
public interface ScoringStrategy {
    /**
     * Calculates a score for a given guess against the current corpus.
     * <p>
     * Higher scores typically indicate better guesses that are more likely
     * to reduce the candidate word set effectively.
     *
     * @param corpus The corpus of candidate words to evaluate against
     * @param guess The potential guess to evaluate
     * @return A numerical score representing the quality of the guess
     */
    double calculateScore(Corpus corpus, NGram guess);
    
    /**
     * Finds the best possible guess from the current corpus based on the
     * implemented scoring algorithm.
     * <p>
     * This method typically evaluates all possible guesses and returns
     * the one with the highest score according to the scoring function.
     *
     * @param corpus The corpus of candidate words to search within
     * @return The NGram representing the best guess according to this strategy
     */
    NGram findBestGuess(Corpus corpus);
} 