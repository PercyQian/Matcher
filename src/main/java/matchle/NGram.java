package matchle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents an immutable sequence of characters with positional information.
 * This class is the core data structure for representing words in the Matchle game,
 * allowing for efficient character comparisons, matching, and iteration.
 * <p>
 * NGram provides methods for accessing characters at specific positions, checking if
 * characters exist at specific positions or anywhere in the sequence, and supports
 * various methods of iteration through the sequence with position information.
 * <p>
 * Instances of this class are immutable and thread-safe.
 */
public final class NGram implements Iterable<IndexedCharacter>, Serializable {
    /** Serialization version UID for consistent serialization across versions */
    private static final long serialVersionUID = 1L;
    
    /** The internal list of characters in this NGram */
    private final ArrayList<Character> ngram;
    
    /** Set of unique characters in this NGram for efficient containment checks */
    private final Set<Character> charset;

    /**
     * Private constructor to create a new NGram from a list of characters.
     * 
     * @param characters The list of characters to include in this NGram
     */
    private NGram(List<Character> characters) {
        this.ngram = new ArrayList<>(characters);
        this.charset = new HashSet<>(characters);
    }

    /**
     * Creates a new NGram from a list of characters.
     * 
     * @param characters The list of characters to include in the NGram
     * @return A new NGram instance
     * @throws IllegalArgumentException if the list contains null characters
     *         with a NullCharacterException as the cause
     * @throws NullPointerException if characters is null
     */
    public static final NGram from(List<Character> characters) {
        NullCharacterException.validate(characters);
        return new NGram(characters);
    }

    /**
     * Creates a new NGram from a string.
     * This method converts the string to a list of characters.
     * 
     * @param word The string to convert to an NGram
     * @return A new NGram instance
     * @throws NullPointerException if the word is null
     */
    public static final NGram from(String word) {
        Objects.requireNonNull(word, "Word cannot be null");
        List<Character> characters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            characters.add(c);
        }
        return from(characters);
    }

    /**
     * Gets the character at the specified index.
     * 
     * @param index The index of the character to retrieve
     * @return The character at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Character get(int index) {
        return ngram.get(index);
    }

    /**
     * Gets the number of characters in this NGram.
     * 
     * @return The size of this NGram
     */
    public int size() {
        return ngram.size();
    }

    /**
     * Checks if the character at the specified index matches the given indexed character.
     * This method compares both the index and the character value.
     * 
     * @param c The indexed character to check
     * @return true if the character at the specified index matches, false otherwise
     */
    public boolean matches(IndexedCharacter c) {
        return ngram.get(c.index()).equals(c.character()); 
    }

    /**
     * Checks if this NGram contains the specified character anywhere.
     * 
     * @param c The character to check for
     * @return true if this NGram contains the character, false otherwise
     */
    public boolean contains(char c) {
        return charset.contains(c);
    }

    /**
     * Checks if this NGram contains the specified character at a different index.
     * This is useful for determining if a character is present but in the wrong position.
     * 
     * @param c The indexed character to check
     * @return true if the character exists in this NGram but at a different index, false otherwise
     */
    public boolean containsElsewhere(IndexedCharacter c) {
        return charset.contains(c.character()) && !matches(c);
    }

    /**
     * Compares this NGram with another object for equality.
     * Two NGrams are equal if they contain the same characters in the same order.
     * 
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NGram)) return false;
        NGram other = (NGram) obj;
        return ngram.equals(other.ngram);
    }

    /**
     * Returns a hash code for this NGram.
     * 
     * @return A hash code value for this NGram
     */
    @Override
    public int hashCode() {
        return Objects.hash(ngram);
    }

    /**
     * Returns a stream of indexed characters from this NGram.
     * Each character is paired with its index in the sequence.
     * 
     * @return A stream of indexed characters
     */
    public Stream<IndexedCharacter> stream() {
        return IntStream.range(0, ngram.size())
                .mapToObj(i -> new IndexedCharacter(i, ngram.get(i)));
    }

    /**
     * Returns an iterator over the indexed characters in this NGram.
     * 
     * @return An iterator over indexed characters
     */
    @Override
    public Iterator<IndexedCharacter> iterator() {
        return new NGramIterator(); 
    }

    /**
     * A private iterator implementation for NGram.
     * This iterates through the characters in the NGram, providing both
     * the character and its position in the sequence.
     */
    private final class NGramIterator implements Iterator<IndexedCharacter>, Serializable {
        /** Serialization version UID */
        private static final long serialVersionUID = 1L;
        
        /** Current position in the NGram */
        private int index = 0;

        /**
         * Checks if there are more characters in the iterator.
         * 
         * @return true if there are more characters, false otherwise
         */
        @Override
        public boolean hasNext() {
            return index < ngram.size();
        }

        /**
         * Returns the next indexed character in the sequence.
         * 
         * @return The next indexed character
         * @throws NoSuchElementException if there are no more characters
         */
        @Override
        public IndexedCharacter next() {
            if (!hasNext()) throw new NoSuchElementException();
            return new IndexedCharacter(index, ngram.get(index++));
        }
    }

    /**
     * Returns a string representation of this NGram.
     * The string consists of all characters in the NGram concatenated together.
     * 
     * @return A string representation of this NGram
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Character c : ngram) {
            sb.append(c);
        }
        return sb.toString();
    }
}
