package matchle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 管理游戏状态的保存和加载
 */
public class GameStateManager {
    
    /**
     * 保存游戏状态到文件
     */
    public static void saveGame(GameState state, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(state);
        }
    }
    
    /**
     * 从文件加载游戏状态
     */
    public static GameState loadGame(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (GameState) ois.readObject();
        }
    }
} 