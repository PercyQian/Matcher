package matchle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Shared game logic for the Matchle word guessing game.
 * This class contains the core game mechanics and state management,
 * and is designed to be used by both console and GUI versions of the game.
 * 
 * The primary responsibilities include:
 * - Managing the game state (corpus, secret key, filters)
 * - Processing guesses and generating feedback
 * - Determining game termination conditions
 * - Suggesting optimal guesses
 */
public class GameLogic {
    /** The original corpus of words used in the game */
    private Corpus corpus;
    
    /** The secret word that players are trying to guess */
    private NGram secretKey;
    
    /** The current filtered corpus of candidate words based on previous guesses */
    private Corpus candidateCorpus;
    
    /** The accumulated filter from all previous guesses */
    private Filter accumulatedFilter;
    
    /**
     * Initializes a new game with the specified corpus and secret key.
     * This method sets up the initial game state with a predefined secret key.
     *
     * @param corpus The corpus of words to use for the game
     * @param secretKey The predefined secret key to use
     */
    public void initialize(Corpus corpus, NGram secretKey) {
        this.corpus = corpus;
        this.secretKey = secretKey;
        this.candidateCorpus = corpus;
        this.accumulatedFilter = null;
    }
    
    /**
     * Initializes a new game with the specified corpus and a randomly selected secret key.
     * This method sets up the initial game state by choosing a random word from the corpus.
     *
     * @param corpus The corpus of words to use for the game
     */
    public void initialize(Corpus corpus) {
        this.corpus = corpus;
        selectRandomKey();
        this.candidateCorpus = corpus;
        this.accumulatedFilter = null;
    }
    
    /**
     * Creates a default corpus of words when none is provided or loading fails.
     * This provides a fallback set of words to ensure the game can still function.
     *
     * @return A Corpus containing a default set of words
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
     * Selects a random word from the corpus to use as the secret key.
     * This method shuffles the corpus and selects the first word.
     * Should only be called during initialization.
     */
    private void selectRandomKey() {
        List<NGram> keys = new ArrayList<>(corpus.corpus());
        Collections.shuffle(keys);
        secretKey = keys.get(0);
    }
    
    /**
     * Checks if the provided guess matches the secret key.
     *
     * @param guess The guess to check against the secret key
     * @return true if the guess is correct, false otherwise
     */
    public boolean isCorrectGuess(NGram guess) {
        return guess.equals(secretKey);
    }
    
    /**
     * Processes a guess and updates the game state accordingly.
     * This method generates a filter based on the guess, updates the accumulated filter,
     * and filters the candidate corpus.
     *
     * @param guess The guess to process
     * @return The Filter generated for this round of guessing
     */
    public Filter processGuess(NGram guess) {
        Filter roundFilter = generateRoundFilter(guess);
        updateAccumulatedFilter(roundFilter);
        updateCandidateCorpus();
        return roundFilter;
    }
    
    /**
     * Generates a filter by comparing a guess against the secret key.
     * The filter represents the pattern of matches between the guess and the key.
     *
     * @param guess The guess to compare against the secret key
     * @return A Filter representing the pattern of matches
     */
    public Filter generateRoundFilter(NGram guess) {
        return NGramMatcher.of(secretKey, guess).match();
    }
    
    /**
     * Updates the accumulated filter by combining it with a new round filter.
     * If no accumulated filter exists yet, the round filter becomes the accumulated filter.
     *
     * @param roundFilter The filter from the current round to add to the accumulated filter
     */
    private void updateAccumulatedFilter(Filter roundFilter) {
        if (accumulatedFilter != null) {
            accumulatedFilter = accumulatedFilter.and(Optional.of(roundFilter));
        } else {
            accumulatedFilter = roundFilter;
        }
    }
    
    /**
     * Updates the candidate corpus by applying the accumulated filter.
     * This reduces the set of possible words based on all guesses so far.
     * If the resulting corpus is empty or null, an empty corpus is created.
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
     * Gets the best possible guess from the current candidate corpus.
     * This uses the corpus's algorithm to determine the optimal guess
     * that will provide the most information.
     *
     * @return The NGram representing the best guess
     */
    public NGram getBestGuess() {
        return candidateCorpus.bestWorstCaseGuess();
    }
    
    /**
     * Checks if the game has terminated by testing ending conditions.
     * The game is considered terminated if there are zero or one candidates remaining.
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean hasGameTerminated() {
        return candidateCorpus.size() <= 1 || candidateCorpus.size() == 0;
    }
    
    /**
     * Gets the secret key that players are trying to guess.
     *
     * @return The NGram representing the secret key
     */
    public NGram getSecretKey() {
        return secretKey;
    }
    
    /**
     * Gets the current candidate corpus containing all possible words that match
     * the accumulated filter.
     *
     * @return The Corpus of candidate words
     */
    public Corpus getCandidateCorpus() {
        return candidateCorpus;
    }
    
    /**
     * Gets the accumulated filter from all rounds played so far.
     *
     * @return The accumulated Filter object
     */
    public Filter getAccumulatedFilter() {
        return accumulatedFilter;
    }
    
    /**
     * Loads a saved game state into this GameLogic instance.
     * This replaces the current game state with the provided saved state.
     *
     * @param state The GameState to load
     */
    public void loadState(GameState state) {
        this.secretKey = state.getSecretKey();
        this.candidateCorpus = state.getCandidateCorpus();
        this.accumulatedFilter = state.getAccumulatedFilter();
    }
    
    /**
     * Creates a GameState object representing the current state of the game.
     * This can be used for saving the game state.
     *
     * @return A new GameState object containing the current game state
     */
    public GameState createGameState() {
        return new GameState(secretKey, candidateCorpus, accumulatedFilter);
    }
} 