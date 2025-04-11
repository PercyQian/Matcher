package matchle.exception;

/**
 * Top-level exception class for Matchle game, all custom exceptions should extend this class
 */
public class MatchleException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public MatchleException(String message) {
        super(message);
    }
    
    public MatchleException(String message, Throwable cause) {
        super(message, cause);
    }
} 