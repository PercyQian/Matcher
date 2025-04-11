package matchle;

import java.io.Serializable;

/**
 * the class to represent the game state, for saving and loading the game
 */
public class GameState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final NGram secretKey;
    private final Corpus candidateCorpus;
    private final Filter accumulatedFilter;
    
    public GameState(NGram secretKey, Corpus candidateCorpus, Filter accumulatedFilter) {
        this.secretKey = secretKey;
        this.candidateCorpus = candidateCorpus;
        this.accumulatedFilter = accumulatedFilter;
    }
    
    public NGram getSecretKey() {
        return secretKey;
    }
    
    public Corpus getCandidateCorpus() {
        return candidateCorpus;
    }
    
    public Filter getAccumulatedFilter() {
        return accumulatedFilter;
    }
} 