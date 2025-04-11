package matchle.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Matchle游戏通用工具类
 */
public final class MatchleUtils {
    
    private MatchleUtils() {
        // 防止实例化
    }
    
    /**
     * 计算两个集合的交集大小
     */
    public static <T> int intersectionSize(Collection<T> c1, Collection<T> c2) {
        Set<T> s1 = new HashSet<>(c1);
        s1.retainAll(c2);
        return s1.size();
    }
    
    /**
     * 计算两个字符串的汉明距离（不同位置的字符数量）
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