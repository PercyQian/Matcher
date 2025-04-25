package matchle;

import java.io.Serializable;

/**
 * A record representing a character at a specific position in a sequence.
 * This class pairs a character with its index, which is essential for
 * the Matchle game when comparing characters at specific positions.
 * <p>
 * Being a record, instances of this class are immutable and thread-safe,
 * with automatic implementations of equals(), hashCode(), and toString().
 */
public record IndexedCharacter(
        /**
         * The position of this character in the sequence (zero-based)
         */
        int index,
        
        /**
         * The character value at the specified position
         */
        Character character
) implements Serializable {
    /** Serialization version UID for consistent serialization across versions */
    private static final long serialVersionUID = 1L;
}
