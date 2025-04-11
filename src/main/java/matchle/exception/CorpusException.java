package matchle.exception;

/**
 * 词库相关的异常
 */
public class CorpusException extends MatchleException {
    
    private static final long serialVersionUID = 1L;
    
    public CorpusException(String message) {
        super(message);
    }
    
    public CorpusException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 当词库为空时抛出的异常
     */
    public static class EmptyCorpusException extends CorpusException {
        private static final long serialVersionUID = 1L;
        
        public EmptyCorpusException() {
            super("Corpus is empty");
        }
    }
    
    /**
     * 当词库中的单词长度不一致时抛出的异常
     */
    public static class InconsistentWordSizeException extends CorpusException {
        private static final long serialVersionUID = 1L;
        
        public InconsistentWordSizeException() {
            super("Words in corpus have inconsistent length");
        }
    }
} 