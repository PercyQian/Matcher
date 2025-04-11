package matchle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CorpusLoader {

    /**
     * Downloads an English word list from a specified URL and constructs a Corpus
     * containing only words of the specified length.
     * 
     * @param wordLength The required word length (e.g., 5)
     * @return A constructed Corpus, or null if no valid words are found
     * 
     * Implementation details:
     * 1. Downloads word list from GitHub
     * 2. Filters for words of exact length
     * 3. Limits to first 300 words for performance
     * 4. Converts to lowercase and creates NGrams
     */
    public static Corpus loadEnglishWords(int wordLength) {
        List<NGram> ngrams = new ArrayList<>();
        try {
            // use a smaller word list URL
            URL url = new URL("https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                ngrams = reader.lines()
                        .map(String::trim)
                        .filter(word -> word.length() == wordLength) // filter words of exact length
                        .map(String::toLowerCase)
                        .limit(300)  // limit to the first 300 words
                        .map(NGram::from)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Corpus corpus = Corpus.Builder.of().addAll(ngrams).build();
        return corpus;
    }

    /**
     * test various scoring functions.
     */
    public static void main(String[] args) {
        // construct a corpus of 5-letter words
        Corpus corpus = loadEnglishWords(5);
        if (corpus == null) {
            System.out.println("Corpus is empty or inconsistent.");
            return;
        }
        System.out.println("Loaded corpus with " + corpus.size() + " 5-letter words.");

        // choose a guess word, for example "route"
        NGram guess = NGram.from("abced");
        System.out.println("Testing score functions for guess: " + guess);

        // test score(key,guess) - take the first word in corpus as an example
        NGram sampleKey = corpus.iterator().next();
        long score = corpus.score(sampleKey, guess);
        System.out.println("Score for key " + sampleKey + " and guess " + guess + " = " + score);

        // test scoreWorstCase(guess) - take the first word in corpus as an example
        long worstCase = corpus.scoreWorstCase(guess);
        System.out.println("Worst-case score for guess " + guess + " = " + worstCase);

        // test scoreAverageCase(guess) - take the first word in corpus as an example
        double avgCase = corpus.scoreAverageCase(guess);
        System.out.println("Average-case score for guess " + guess + " = " + avgCase);

        // test bestWorstCaseGuess() and bestAverageCaseGuess()
        NGram bestWorst = corpus.bestWorstCaseGuess();
        NGram bestAverage = corpus.bestAverageCaseGuess();
        System.out.println("Best worst-case guess: " + bestWorst);
        System.out.println("Best average-case guess: " + bestAverage);

        // test hard case
        testHardCase();
    }

    /**
     * test the scoring functions for hard case.
     */
    public static void testHardCase() {
        Corpus corpus = loadEnglishWords(5);
        if (corpus == null) {
            System.out.println("Corpus is empty or inconsistent.");
            return;
        }
        
        System.out.println("Loaded corpus with " + corpus.size() + " 5-letter words.");
        System.out.println("\n=== Testing Hard Case ===");
        System.out.println("Loaded special corpus with " + corpus.size() + " words.");

        // choose "where" as the test word
        NGram guess = NGram.from("where");
        System.out.println("Testing score functions for guess: " + guess);

        // test the score for each possible key
        for (NGram key : corpus) {
            long score = corpus.score(key, guess);
            System.out.println("Score for key " + key + " and guess " + guess + " = " + score);
        }

        // test the best guess
        NGram bestWorst = corpus.bestWorstCaseGuess();
        System.out.println("Best worst-case guess: " + bestWorst);
    }
}
