package matchle.exception;

/**
 * Matchle游戏的顶级异常类，所有自定义异常都应继承此类
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