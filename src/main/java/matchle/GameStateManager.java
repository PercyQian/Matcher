package matchle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Manages the persistence of game state to and from storage.
 * <p>
 * This utility class provides methods for saving and loading game states,
 * allowing players to resume games across sessions. It uses Java's serialization
 * mechanism to write the complete game state to a file and later restore it.
 * <p>
 * All methods in this class are static, and the class cannot be instantiated.
 * The methods handle the low-level details of file I/O and object serialization,
 * providing a simple interface for the rest of the application.
 */
public class GameStateManager {
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GameStateManager() {
        // Prevent instantiation
    }
    
    /**
     * Saves a game state to a file.
     * <p>
     * This method serializes the provided GameState object and writes it to the
     * specified file. The file will be created if it doesn't exist, or overwritten
     * if it does.
     *
     * @param state The GameState object to save
     * @param filename The path to the file where the state will be saved
     * @throws IOException If an I/O error occurs during writing
     * @throws NullPointerException If state or filename is null
     */
    public static void saveGame(GameState state, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(state);
        }
    }
    
    /**
     * Loads a game state from a file.
     * <p>
     * This method reads and deserializes a GameState object from the specified file.
     * The file must contain a valid serialized GameState object that was previously
     * saved with the saveGame method.
     *
     * @param filename The path to the file containing the saved state
     * @return The restored GameState object
     * @throws IOException If an I/O error occurs during reading
     * @throws ClassNotFoundException If the class of the serialized object cannot be found
     * @throws ClassCastException If the serialized object is not a GameState
     * @throws NullPointerException If filename is null
     */
    public static GameState loadGame(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (GameState) ois.readObject();
        }
    }
} 