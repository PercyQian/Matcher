package matchle.util;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchleUtilsTest {
    
    @Test
    public void testIntersectionSize() {
        // 测试两个集合的交集大小计算
        List<String> list1 = Arrays.asList("a", "b", "c", "d");
        List<String> list2 = Arrays.asList("c", "d", "e", "f");
        
        int intersectionSize = MatchleUtils.intersectionSize(list1, list2);
        assertEquals("Intersection size should be 2", 2, intersectionSize);
    }
    
    @Test
    public void testIntersectionSizeWithEmptyCollection() {
        // 测试空集合的交集
        List<String> emptyList = Arrays.asList();
        List<String> nonEmptyList = Arrays.asList("a", "b", "c");
        
        int intersectionSize = MatchleUtils.intersectionSize(emptyList, nonEmptyList);
        assertEquals("Intersection with empty list should be 0", 0, intersectionSize);
    }
    
    @Test
    public void testIntersectionSizeWithNoCommonElements() {
        // 测试没有共同元素的集合
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("d", "e", "f");
        
        int intersectionSize = MatchleUtils.intersectionSize(list1, list2);
        assertEquals("Intersection with no common elements should be 0", 0, intersectionSize);
    }
    
    @Test
    public void testHammingDistance() {
        // 测试汉明距离计算
        String s1 = "hello";
        String s2 = "hallo";
        
        int distance = MatchleUtils.hammingDistance(s1, s2);
        assertEquals("Hamming distance should be 1", 1, distance);
    }
    
    @Test
    public void testHammingDistanceWithIdenticalStrings() {
        // 测试相同字符串的汉明距离
        String s = "hello";
        
        int distance = MatchleUtils.hammingDistance(s, s);
        assertEquals("Hamming distance of identical strings should be 0", 0, distance);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHammingDistanceWithDifferentLengths() {
        // 测试不同长度字符串的汉明距离，应抛出异常
        String s1 = "hello";
        String s2 = "hi";
        
        MatchleUtils.hammingDistance(s1, s2);
    }
} 