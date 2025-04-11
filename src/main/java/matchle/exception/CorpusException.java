package matchle.exception;

/**
 * Corpus-related exceptions
 */
public class CorpusException extends MatchleException {
    
    public CorpusException(String message) {
        super(message);
    }
    
    public CorpusException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Exception thrown when the corpus is empty
     */
    public static class EmptyCorpusException extends CorpusException {
        
        public EmptyCorpusException() {
            super("Corpus is empty");
        }
    }
    
    /**
     * Exception thrown when words in the corpus have inconsistent lengths
     */
    public static class InconsistentWordSizeException extends CorpusException {
        
        public InconsistentWordSizeException() {
            super("Words in corpus have inconsistent length");
        }
    }
} 