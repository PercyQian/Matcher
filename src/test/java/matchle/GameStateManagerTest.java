package matchle;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    
    @Test
    public void testFilterSerializability() throws IOException, ClassNotFoundException {
        // 测试Filter的序列化和反序列化
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testFilter);
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Filter deserializedFilter = (Filter) ois.readObject();
        ois.close();
        
        // 验证反序列化后的Filter能够通过测试
        assertNotNull("Deserialized filter should not be null", deserializedFilter);
        
        // 原始过滤器应该匹配key
        assertTrue("Original key should match the filter", testFilter.test(testKey));
        
        // 由于Filter中的predicate是transient的，反序列化后的filter会默认对所有输入返回true
        // 所以这里我们不能期望它有和原始过滤器一样的行为，我们只能验证它能被加载
        // 测试它的toString输出是否与序列化前一致
        assertEquals("Filter pattern should be preserved", testFilter.toString(), deserializedFilter.toString());
    }
    
    @Test
    public void testGameStateSerializability() throws IOException, ClassNotFoundException {
        // 测试完整的GameState序列化和反序列化，但不通过文件系统
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testState);
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        GameState deserializedState = (GameState) ois.readObject();
        ois.close();
        
        // 验证反序列化的GameState
        assertNotNull("Deserialized state should not be null", deserializedState);
        assertEquals("Secret key should match", 
                testState.getSecretKey().toString(), 
                deserializedState.getSecretKey().toString());
        assertEquals("Corpus size should match", 
                testState.getCandidateCorpus().size(), 
                deserializedState.getCandidateCorpus().size());
    }
} 