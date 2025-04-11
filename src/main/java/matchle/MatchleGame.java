package matchle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MatchleGame {
    private Corpus corpus;
    private NGram key;
    private Corpus candidateCorpus;
    private Optional<Filter> accumulatedFilter;
    private int maxRounds = 10;
    
    public static void main(String[] args) {
        MatchleGame game = new MatchleGame();
        game.initialize();
        game.play();
    }
    
    private void initialize() {
        loadCorpus();
        selectRandomKey();
        initializeGameState();
    }
    
    private void loadCorpus() {
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            System.out.println("Corpus is empty or invalid.");
            corpus = createDefaultCorpus();
        }
    }
    
    private Corpus createDefaultCorpus() {
        return Corpus.Builder.of()
                .add(NGram.from("rebus"))
                .add(NGram.from("redux"))
                .add(NGram.from("route"))
                .add(NGram.from("hello"))
                .build();
    }
    
    private void selectRandomKey() {
        List<NGram> keys = new ArrayList<>(corpus.corpus());
        Collections.shuffle(keys);
        key = keys.get(0);
        System.out.println("Secret key (hidden): " + key);
    }
    
    private void initializeGameState() {
        accumulatedFilter = Optional.empty();
        candidateCorpus = corpus;
    }
    
    private void play() {
        for (int round = 1; round <= maxRounds; round++) {
            System.out.println("==== Round " + round + " ====");
            if (playRound(round)) {
                return; // Game over
            }
        }
        System.out.println("Maximum rounds reached. The key was: " + key);
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
        NGram guess = candidateCorpus.bestWorstCaseGuess();
        System.out.println("Best guess: " + guess);
        return guess;
    }
    
    private boolean isCorrectGuess(NGram guess) {
        if (guess.equals(key)) {
            System.out.println("Correct guess! The key is: " + key);
            return true;
        }
        return false;
    }
    
    private void updateGameState(NGram guess) {
        Filter roundFilter = NGramMatcher.of(key, guess).match();
        System.out.println("Round filter: " + roundFilter);
        
        updateAccumulatedFilter(roundFilter);
        updateCandidateCorpus();
    }
    
    private void updateAccumulatedFilter(Filter roundFilter) {
        if (accumulatedFilter.isPresent()) {
            accumulatedFilter = Optional.of(accumulatedFilter.get().and(Optional.of(roundFilter)));
        } else {
            accumulatedFilter = Optional.of(roundFilter);
        }
    }
    
    private void updateCandidateCorpus() {
        Corpus newCorpus = Corpus.Builder.of(candidateCorpus)
                .filter(accumulatedFilter.get())
                .build();
        
        if (newCorpus == null) {
            System.out.println("No valid candidates. The key was: " + key);
            candidateCorpus = Corpus.Builder.of().build(); // Empty corpus
        } else {
            candidateCorpus = newCorpus;
        }
        
        System.out.println("Remaining candidate count: " + candidateCorpus.size());
    }
    
    private boolean checkGameTermination() {
        // Check if only one candidate remains
        if (candidateCorpus.size() == 1) {
            NGram remaining = candidateCorpus.corpus().iterator().next();
            System.out.println("Candidate corpus reduced to one: " + remaining);
            if (remaining.equals(key)) {
                System.out.println("Found key: " + key);
            } else {
                System.out.println("Remaining candidate does not match key. Key was: " + key);
            }
            return true;
        }
        
        // Check if the candidate corpus is empty
        if (candidateCorpus.size() == 0) {
            System.out.println("No candidates remain. The key was: " + key);
            return true;
        }
        
        return false;
    }
}
