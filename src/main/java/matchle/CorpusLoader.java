package matchle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for loading word corpora from various sources
 */
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
        
        if (ngrams.isEmpty()) {
            return null;
        }
        
        return Corpus.Builder.of().addAll(ngrams).build();
    }
}
