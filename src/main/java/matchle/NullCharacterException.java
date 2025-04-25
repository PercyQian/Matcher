package matchle;

import java.util.List;
import java.util.Objects;

/**
 * Exception thrown when a character sequence contains a null character.
 * <p>
 * This exception is used in the Matchle game to ensure that all NGram
 * objects contain valid, non-null characters. It provides information
 * about the position of the null character that caused the exception.
 */
public final class NullCharacterException extends Exception {
    /** Serialization version UID for consistent serialization across versions */
    private static final long serialVersionUID = 1L;
    
    /** The index at which the null character was found */
    private final int index;

    /**
     * Constructs a new exception with the specified index.
     *
     * @param index The position of the null character
     */
    public NullCharacterException(int index) {
        super("Null character found at index: " + index);
        this.index = index;
    }

    /**
     * Returns the index at which the null character was found.
     *
     * @return The position of the null character
     */
    public int getIndex() {
        return index;
    }

    /**
     * Validates that a list of characters does not contain any null elements.
     * <p>
     * This method checks each character in the provided list and throws an
     * exception if a null element is found. This is used to ensure the integrity
     * of NGram objects, which cannot contain null characters.
     *
     * @param ngram The list of characters to validate
     * @return The validated list of characters (unchanged)
     * @throws NullPointerException if the list itself is null
     * @throws IllegalArgumentException if the list contains any null elements,
     *         with a NullCharacterException as the cause
     */
    public static List<Character> validate(List<Character> ngram) {
        Objects.requireNonNull(ngram, "NGram list cannot be null");

        for (int i = 0; i < ngram.size(); i++) {
            if (ngram.get(i) == null) {
                throw new IllegalArgumentException(new NullCharacterException(i));
            }
        }
        return ngram;
    }
}
