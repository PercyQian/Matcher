package matchle;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import matchle.exception.CorpusException;

/**
 * 测试Corpus类的基本功能
 */
public class CorpusTest {
    private Corpus corpus;
    private NGram word1, word2, word3, nonExistent;

    @Before
    public void setUp() {
        word1 = NGram.from("apple");
        word2 = NGram.from("pearl");
        word3 = NGram.from("grape");

        corpus = Corpus.Builder.of()
                .add(word1)
                .add(word2)
                .add(word3)
                .build();
    }

    @Test
    public void testContains() {
        assertTrue("Corpus should contain 'apple'", corpus.contains(word1));
        assertTrue("Corpus should contain 'pearl'", corpus.contains(word2));
        assertTrue("Corpus should contain 'grape'", corpus.contains(word3));
    }

    @Test
    public void testNotContains() {
        nonExistent = NGram.from("table");
        assertFalse("Corpus should NOT contain 'table'", corpus.contains(nonExistent));
    }

    @Test
    public void testWordSize() {
        assertEquals("Word size should be 5 for all words in corpus", 5, corpus.wordSize());
    }

    @Test
    public void testIterator() {
        Set<NGram> words = new HashSet<>();
        int count = 0;
        for (NGram word : corpus) {
            words.add(word);
            count++;
        }
        
        assertEquals("Iterator should return exactly 3 elements", 3, count);
    }

    @Test
    public void testCorpusContents() {
        Set<NGram> words = corpus.corpus();
        
        assertTrue("Corpus should contain all 3 added words",
                words.contains(word1) && words.contains(word2) && words.contains(word3));
    }

    @Test
    public void testEmptyCorpus() {
        try {
            Corpus emptyCorpus = Corpus.Builder.of().build();
            assertNull("Empty corpus should be null when no words are added", emptyCorpus);
        } catch (CorpusException.EmptyCorpusException e) {
            assertTrue("Exception message should mention empty corpus", 
                     e.getMessage().toLowerCase().contains("empty"));
        }
    }

    @Test
    public void testInconsistentWordSize() {
        try {
            Corpus.Builder builder = Corpus.Builder.of()
                    .add(NGram.from("apple"))
                    .add(NGram.from("orange"));
            
            Corpus result = builder.build();
            assertNull("Corpus should be null if word sizes are inconsistent", result);
        } catch (CorpusException.InconsistentWordSizeException e) {
            assertTrue("Exception message should mention inconsistent length", 
                     e.getMessage().toLowerCase().contains("inconsistent"));
        }
    }

    @Test
    public void testAddNullWord() {
        Corpus.Builder builder = Corpus.Builder.of();
        boolean exceptionThrown = false;
        try {
            builder.add(null);
        } catch (NullPointerException expected) {
            exceptionThrown = true;
        }
        assertTrue("Adding null word should throw NullPointerException", exceptionThrown);
    }
}
