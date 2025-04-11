package matchle.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class providing common functions for the Matchle game
 */
public final class MatchleUtils {
    
    private MatchleUtils() {
        // 防止实例化
    }
    
    /**
     * Calculate the intersection size of two collections
     * 
     * @param c1 First collection
     * @param c2 Second collection
     * @return The size of the intersection
     */
    public static <T> int intersectionSize(Collection<T> c1, Collection<T> c2) {
        Set<T> intersection = new HashSet<>(c1);
        intersection.retainAll(c2);
        return intersection.size();
    }
    
    /**
     * Calculate the Hamming distance between two strings (the number of different characters at the same positions)
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