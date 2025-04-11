package matchle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Shared game logic for the Matchle game,
 * to be used by both console and GUI versions.
 */
public class GameLogic {
    private Corpus corpus;
    private NGram secretKey;
    private Corpus candidateCorpus;
    private Filter accumulatedFilter;
    
    /**
     * Initialize a new game with the specified parameters
     */
    public void initialize(Corpus corpus, NGram secretKey) {
        this.corpus = corpus;
        this.secretKey = secretKey;
        this.candidateCorpus = corpus;
        this.accumulatedFilter = null;
    }
    
    /**
     * Initialize a new game with random key selection
     */
    public void initialize(Corpus corpus) {
        this.corpus = corpus;
        selectRandomKey();
        this.candidateCorpus = corpus;
        this.accumulatedFilter = null;
    }
    
    /**
     * Create a default corpus when none is provided
     */
    public Corpus createDefaultCorpus() {
        return Corpus.Builder.of()
                .add(NGram.from("rebus"))
                .add(NGram.from("redux"))
                .add(NGram.from("route"))
                .add(NGram.from("hello"))
                .build();
    }
    
    /**
     * Select a random key from the corpus
     */
    private void selectRandomKey() {
        List<NGram> keys = new ArrayList<>(corpus.corpus());
        Collections.shuffle(keys);
        secretKey = keys.get(0);
    }
    
    /**
     * Check if the guess matches the secret key
     */
    public boolean isCorrectGuess(NGram guess) {
        return guess.equals(secretKey);
    }
    
    /**
     * Process a guess and update game state
     * @return Generated filter for this round
     */
    public Filter processGuess(NGram guess) {
        Filter roundFilter = generateRoundFilter(guess);
        updateAccumulatedFilter(roundFilter);
        updateCandidateCorpus();
        return roundFilter;
    }
    
    /**
     * Generate a filter for a guess against the secret key
     */
    public Filter generateRoundFilter(NGram guess) {
        return NGramMatcher.of(secretKey, guess).match();
    }
    
    /**
     * Update accumulated filter with a new round filter
     */
    private void updateAccumulatedFilter(Filter roundFilter) {
        if (accumulatedFilter != null) {
            accumulatedFilter = accumulatedFilter.and(Optional.of(roundFilter));
        } else {
            accumulatedFilter = roundFilter;
        }
    }
    
    /**
     * Update candidate corpus based on accumulated filter
     */
    private void updateCandidateCorpus() {
        if (accumulatedFilter == null) {
            return;
        }
        
        Corpus newCorpus = Corpus.Builder.of(candidateCorpus)
                .filter(accumulatedFilter)
                .build();
        
        if (newCorpus == null || newCorpus.size() == 0) {
            candidateCorpus = Corpus.Builder.of().build(); // Empty corpus
        } else {
            candidateCorpus = newCorpus;
        }
    }
    
    /**
     * Get the best guess from current candidate corpus
     */
    public NGram getBestGuess() {
        return candidateCorpus.bestWorstCaseGuess();
    }
    
    /**
     * Check if game has terminated (one candidate or empty corpus)
     */
    public boolean hasGameTerminated() {
        return candidateCorpus.size() <= 1 || candidateCorpus.size() == 0;
    }
    
    /**
     * Get the secret key
     */
    public NGram getSecretKey() {
        return secretKey;
    }
    
    /**
     * Get the candidate corpus
     */
    public Corpus getCandidateCorpus() {
        return candidateCorpus;
    }
    
    /**
     * Get the accumulated filter
     */
    public Filter getAccumulatedFilter() {
        return accumulatedFilter;
    }
    
    /**
     * Load state from a GameState object
     */
    public void loadState(GameState state) {
        this.secretKey = state.getSecretKey();
        this.candidateCorpus = state.getCandidateCorpus();
        this.accumulatedFilter = state.getAccumulatedFilter();
    }
    
    /**
     * Create a GameState object from current state
     */
    public GameState createGameState() {
        return new GameState(secretKey, candidateCorpus, accumulatedFilter);
    }
} 