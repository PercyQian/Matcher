package matchle;

import java.io.Serializable;

/**
 * Represents the complete state of a Matchle game session.
 * <p>
 * This class encapsulates all essential components needed to save and restore
 * a game in progress, including:
 * <ul>
 *   <li>The secret key (target word) that the player is trying to guess</li>
 *   <li>The current corpus of candidate words that match all previous guesses</li>
 *   <li>The accumulated filter representing all constraints from previous guesses</li>
 * </ul>
 * <p>
 * GameState is immutable and implements Serializable to support persistence
 * through Java's serialization mechanism, allowing games to be saved to disk
 * and loaded later.
 */
public class GameState implements Serializable {
    
    /** Serialization version UID for consistent serialization across versions */
    private static final long serialVersionUID = 1L;
    
    /** The secret key (target word) that the player is trying to guess */
    private final NGram secretKey;
    
    /** The current corpus of candidate words that match all constraints */
    private final Corpus candidateCorpus;
    
    /** The accumulated filter representing all constraints from previous guesses */
    private final Filter accumulatedFilter;
    
    /**
     * Constructs a new GameState with the specified components.
     * 
     * @param secretKey The secret key (target word) for the game
     * @param candidateCorpus The current corpus of candidate words
     * @param accumulatedFilter The accumulated filter from previous guesses
     */
    public GameState(NGram secretKey, Corpus candidateCorpus, Filter accumulatedFilter) {
        this.secretKey = secretKey;
        this.candidateCorpus = candidateCorpus;
        this.accumulatedFilter = accumulatedFilter;
    }
    
    /**
     * Gets the secret key that the player is trying to guess.
     * 
     * @return The NGram representing the secret key
     */
    public NGram getSecretKey() {
        return secretKey;
    }
    
    /**
     * Gets the current corpus of candidate words that match all constraints.
     * 
     * @return The Corpus of candidate words
     */
    public Corpus getCandidateCorpus() {
        return candidateCorpus;
    }
    
    /**
     * Gets the accumulated filter representing all constraints from previous guesses.
     * 
     * @return The accumulated Filter object
     */
    public Filter getAccumulatedFilter() {
        return accumulatedFilter;
    }
} 