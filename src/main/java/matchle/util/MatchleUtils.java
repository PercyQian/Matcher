package matchle.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class providing common functions for the Matchle game.
 * <p>
 * This class contains static utility methods for various game-related operations
 * such as collection manipulation and string distance calculations. These methods
 * are used across different components of the game to provide consistent behavior.
 * <p>
 * All methods in this class are designed to be thread-safe and side-effect free.
 * This class cannot be instantiated as all methods are static.
 */
public final class MatchleUtils {
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * This constructor is never called as all methods are static.
     */
    private MatchleUtils() {
        // prevent instantiation
    }
    
    /**
     * Calculates the size of the intersection between two collections.
     * <p>
     * This method determines how many elements are common to both collections.
     * It creates a temporary set from the first collection and then retains only
     * the elements that also exist in the second collection.
     * 
     * @param <T> The type of elements in the collections
     * @param c1 The first collection
     * @param c2 The second collection
     * @return The number of elements common to both collections
     * @throws NullPointerException if either collection is null
     */
    public static <T> int intersectionSize(Collection<T> c1, Collection<T> c2) {
        Set<T> intersection = new HashSet<>(c1);
        intersection.retainAll(c2);
        return intersection.size();
    }
    
    /**
     * Calculates the Hamming distance between two strings.
     * <p>
     * The Hamming distance is the number of positions at which the corresponding
     * characters in the two strings differ. This measurement is useful in the 
     * Matchle game for comparing word similarity and determining feedback patterns.
     * <p>
     * For example, the Hamming distance between "karolin" and "kathrin" is 3.
     *
     * @param s1 The first string
     * @param s2 The second string
     * @return The number of positions where the characters differ
     * @throws IllegalArgumentException if the strings have different lengths
     * @throws NullPointerException if either string is null
     */
    public static int hammingDistance(String s1, String s2) {
        if (s1.length() != s2.length()) {
            throw new IllegalArgumentException("Strings must be of equal length");
        }
        
        int distance = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }
} 