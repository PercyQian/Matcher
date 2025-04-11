package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameStateManagerTest {
    
    private NGram testKey;
    private Corpus testCorpus;
    private Filter testFilter;
    private GameState testState;
    private final String testFilename = "test_gamestate.dat";
    
    @Before
    public void setUp() {
        // 设置测试数据
        testKey = NGram.from("hello");
        testCorpus = Corpus.Builder.of()
                .add(NGram.from("hello"))
                .add(NGram.from("world"))
                .build();
        testFilter = NGramMatcher.of(testKey, NGram.from("world")).match();
        
        // 创建测试用GameState
        testState = new GameState(testKey, testCorpus, testFilter);
    }
    
    @After
    public void tearDown() {
        // 测试后清理创建的文件
        File testFile = new File(testFilename);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
    
    @Test
    public void testSaveAndLoadGame() throws IOException, ClassNotFoundException {
        // 保存游戏状态
        GameStateManager.saveGame(testState, testFilename);
        
        // 验证文件已创建
        File savedFile = new File(testFilename);
        assertTrue("File should be created", savedFile.exists());
        
        // 加载游戏状态
        GameState loadedState = GameStateManager.loadGame(testFilename);
        
        // 验证加载的状态与保存的状态一致
        assertNotNull("Loaded state should not be null", loadedState);
        assertEquals("Secret key should match", 
                testState.getSecretKey().toString(), 
                loadedState.getSecretKey().toString());
        assertEquals("Corpus size should match", 
                testState.getCandidateCorpus().size(), 
                loadedState.getCandidateCorpus().size());
    }
    
    @Test(expected = IOException.class)
    public void testLoadNonExistentFile() throws IOException, ClassNotFoundException {
        // 尝试加载不存在的文件，应抛出IOException
        GameStateManager.loadGame("non_existent_file.dat");
    }
} 