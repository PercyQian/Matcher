package matchle;

/**
 * The main console implementation of the Matchle word guessing game.
 * This class manages the game flow, user interaction through console,
 * and integrates with the GameLogic for core game functionality.
 * 
 * The game involves guessing a secret word by making a series of guesses,
 * with feedback provided after each guess to narrow down the possibilities.
 */
public class MatchleGame {
    /** Game logic component that handles the core game mechanics */
    private GameLogic gameLogic;
    
    /** The corpus of words used in the game */
    private Corpus corpus;
    
    /** Maximum number of rounds allowed in a game */
    private int maxRounds = 10;
    
    /**
     * Constructs a new Matchle game with default settings.
     * Initializes the game logic component.
     */
    public MatchleGame() {
        this.gameLogic = new GameLogic();
    }
    
    /**
     * Entry point for running the game from the command line.
     * Creates and starts a new Matchle game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        MatchleGame game = new MatchleGame();
        game.playGame();
    }
    
    /**
     * Public method to start the game.
     * Initializes the game state and begins the game loop.
     */
    public void playGame() {
        initialize();
        play();
    }
    
    /**
     * Initializes the game by loading the corpus and setting up the game logic.
     * This method should be called before starting a game.
     */
    public void initialize() {
        loadCorpus();
        gameLogic.initialize(corpus);
        System.out.println("Secret key (hidden): " + gameLogic.getSecretKey());
    }
    
    /**
     * Loads the corpus of words for the game.
     * If loading fails, falls back to a default corpus.
     */
    private void loadCorpus() {
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            System.out.println("Corpus is empty or invalid.");
            corpus = gameLogic.createDefaultCorpus();
        }
    }
    
    /**
     * Main game loop that runs until the game ends or maximum rounds are reached.
     * For each round, it handles a player's turn and checks for game termination.
     */
    private void play() {
        for (int round = 1; round <= maxRounds; round++) {
            System.out.println("==== Round " + round + " ====");
            if (playRound(round)) {
                return; // Game over
            }
        }
        System.out.println("Maximum rounds reached. The key was: " + gameLogic.getSecretKey());
    }
    
    /**
     * Handles a single round of gameplay.
     * Makes a guess, checks if it's correct, and updates the game state.
     *
     * @param round The current round number
     * @return true if the game has ended, false otherwise
     */
    private boolean playRound(int round) {
        NGram guess = makeGuess();
        
        if (isCorrectGuess(guess)) {
            return true; // Game over
        }
        
        updateGameState(guess);
        
        return checkGameTermination();
    }
    
    /**
     * Generates the best possible guess based on the current game state.
     *
     * @return The NGram representing the best guess
     */
    private NGram makeGuess() {
        NGram guess = gameLogic.getBestGuess();
        System.out.println("Best guess: " + guess);
        return guess;
    }
    
    /**
     * Checks if a guess matches the secret key.
     * Outputs a success message if the guess is correct.
     *
     * @param guess The guess to check
     * @return true if the guess is correct, false otherwise
     */
    private boolean isCorrectGuess(NGram guess) {
        if (gameLogic.isCorrectGuess(guess)) {
            System.out.println("Correct guess! The key is: " + gameLogic.getSecretKey());
            return true;
        }
        return false;
    }
    
    /**
     * Updates the game state based on a new guess.
     * Processes the guess, generates a filter, and updates the candidate corpus.
     *
     * @param guess The guess used to update the game state
     */
    public void updateGameState(NGram guess) {
        Filter roundFilter = gameLogic.processGuess(guess);
        System.out.println("Round filter: " + roundFilter);
        System.out.println("Remaining candidate count: " + gameLogic.getCandidateCorpus().size());
    }
    
    /**
     * Checks if the game has terminated by testing various ending conditions.
     *
     * @return true if the game has ended, false otherwise
     */
    public boolean checkGameTermination() {
        return handleSingleCandidate() || handleEmptyCorpus();
    }
    
    /**
     * Handles the case when only one candidate word remains.
     * Checks if this candidate matches the secret key and outputs appropriate messages.
     *
     * @return true if there is only one candidate remaining, false otherwise
     */
    public boolean handleSingleCandidate() {
        Corpus candidateCorpus = gameLogic.getCandidateCorpus();
        if (candidateCorpus.size() != 1) {
            return false;
        }
        
        NGram remaining = candidateCorpus.corpus().iterator().next();
        System.out.println("Candidate corpus reduced to one: " + remaining);
        
        if (remaining.equals(gameLogic.getSecretKey())) {
            System.out.println("Found key: " + gameLogic.getSecretKey());
        } else {
            System.out.println("Remaining candidate does not match key. Key was: " + gameLogic.getSecretKey());
        }
        
        return true;
    }
    
    /**
     * Handles the case when no candidate words remain.
     * Outputs a message with the secret key.
     *
     * @return true if no candidates remain, false otherwise
     */
    public boolean handleEmptyCorpus() {
        if (gameLogic.getCandidateCorpus().size() == 0) {
            System.out.println("No candidates remain. The key was: " + gameLogic.getSecretKey());
            return true;
        }
        
        return false;
    }

    /**
     * Gets the accumulated filter from all rounds played so far.
     *
     * @return The accumulated Filter object
     */
    public Filter getAccumulatedFilter() {
        return gameLogic.getAccumulatedFilter();
    }

    /**
     * Gets the current candidate corpus containing all possible words that match
     * the accumulated filter.
     *
     * @return The Corpus of candidate words
     */
    public Corpus getCandidateCorpus() {
        return gameLogic.getCandidateCorpus();
    }

    /**
     * Gets the initial corpus of words before any filtering.
     *
     * @return The initial Corpus
     */
    public Corpus getInitialCorpus() {
        return corpus;
    }

    /**
     * Gets the best guess based on the current game state.
     *
     * @return The NGram representing the best guess
     */
    public NGram getBestGuess() {
        return gameLogic.getBestGuess();
    }

    /**
     * Gets the secret key that players are trying to guess.
     *
     * @return The NGram representing the secret key
     */
    public NGram getSecretKey() {
        return gameLogic.getSecretKey();
    }
}
