package matchle.exception;

import matchle.Corpus;
import matchle.Filter;
import matchle.GameLogic;
import matchle.NGram;
import matchle.NullCharacterException;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一的异常测试类，包含所有Matchle游戏相关的异常测试
 */
public class MatchleExceptionTests {
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullCharacterException() {
        List<Character> chars = new ArrayList<>();
        chars.add(null);
        NGram.from(chars);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStringException() {
        String emptyStr = "";
        NGram.from(emptyStr);
    }
    
    @Test(expected = CorpusException.EmptyCorpusException.class)
    public void testEmptyCorpusException() {
        Corpus.Builder.of().build();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullCorpusException() {
        Corpus.Builder.of(null);
    }
    
    @Test(expected = CorpusException.InconsistentWordSizeException.class)
    public void testInconsistentWordSizeException() {
        Corpus.Builder builder = Corpus.Builder.of();
        builder.add(NGram.from("hello"));
        builder.add(NGram.from("a"));
        builder.build();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFilterCreation() {
        Filter.from(null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testGameStateException() {
        GameLogic gameLogic = new GameLogic();
        gameLogic.getBestGuess(); // 在初始化之前调用
    }
    
    @Test
    public void testExceptionMessages() {
        try {
            List<Character> chars = new ArrayList<>();
            chars.add(null);
            NGram.from(chars);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof NullCharacterException);
            assertTrue(e.getCause().getMessage().contains("Null character found at index: 0"));
        }
        
        try {
            Corpus.Builder.of().build();
            fail("Should throw CorpusException");
        } catch (CorpusException e) {
            assertTrue(e instanceof CorpusException.EmptyCorpusException);
        }
    }
} 