package matchle;

public class MatchleGame {
    private GameLogic gameLogic;
    private Corpus corpus;
    private int maxRounds = 10;
    
    public MatchleGame() {
        this.gameLogic = new GameLogic();
    }
    
    public static void main(String[] args) {
        MatchleGame game = new MatchleGame();
        game.initialize();
        game.play();
    }
    
    private void initialize() {
        loadCorpus();
        gameLogic.initialize(corpus);
        System.out.println("Secret key (hidden): " + gameLogic.getSecretKey());
    }
    
    private void loadCorpus() {
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            System.out.println("Corpus is empty or invalid.");
            corpus = gameLogic.createDefaultCorpus();
        }
    }
    
    private void play() {
        for (int round = 1; round <= maxRounds; round++) {
            System.out.println("==== Round " + round + " ====");
            if (playRound(round)) {
                return; // Game over
            }
        }
        System.out.println("Maximum rounds reached. The key was: " + gameLogic.getSecretKey());
    }
    
    private boolean playRound(int round) {
        NGram guess = makeGuess();
        
        if (isCorrectGuess(guess)) {
            return true; // Game over
        }
        
        updateGameState(guess);
        
        return checkGameTermination();
    }
    
    private NGram makeGuess() {
        NGram guess = gameLogic.getBestGuess();
        System.out.println("Best guess: " + guess);
        return guess;
    }
    
    private boolean isCorrectGuess(NGram guess) {
        if (gameLogic.isCorrectGuess(guess)) {
            System.out.println("Correct guess! The key is: " + gameLogic.getSecretKey());
            return true;
        }
        return false;
    }
    
    private void updateGameState(NGram guess) {
        Filter roundFilter = gameLogic.processGuess(guess);
        System.out.println("Round filter: " + roundFilter);
        System.out.println("Remaining candidate count: " + gameLogic.getCandidateCorpus().size());
    }
    
    private boolean checkGameTermination() {
        return handleSingleCandidate() || handleEmptyCorpus();
    }
    
    private boolean handleSingleCandidate() {
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
    
    private boolean handleEmptyCorpus() {
        if (gameLogic.getCandidateCorpus().size() == 0) {
            System.out.println("No candidates remain. The key was: " + gameLogic.getSecretKey());
            return true;
        }
        
        return false;
    }
}
