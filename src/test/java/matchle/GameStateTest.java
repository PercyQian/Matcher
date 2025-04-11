package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public class GameStateTest {
    
    private NGram testKey;
    private Corpus testCorpus;
    private Filter testFilter;
    private GameState gameState;
    
    @Before
    public void setUp() {
        // 设置测试数据
        testKey = NGram.from("hello");
        testCorpus = Corpus.Builder.of()
                .add(NGram.from("hello"))
                .add(NGram.from("world"))
                .build();
        testFilter = NGramMatcher.of(testKey, NGram.from("world")).match();
        
        // 创建GameState实例
        gameState = new GameState(testKey, testCorpus, testFilter);
    }
    
    @Test
    public void testGetSecretKey() {
        assertEquals("Secret key should be 'hello'", testKey, gameState.getSecretKey());
    }
    
    @Test
    public void testGetCandidateCorpus() {
        assertEquals("Candidate corpus should match", testCorpus, gameState.getCandidateCorpus());
    }
    
    @Test
    public void testGetAccumulatedFilter() {
        assertEquals("Accumulated filter should match", testFilter, gameState.getAccumulatedFilter());
    }
    
    @Test
    public void testSerializable() {
        // 验证GameState实现了Serializable接口
        assertTrue("GameState should implement Serializable", 
                gameState instanceof java.io.Serializable);
    }
} 