package matchle;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import matchle.exception.CorpusException;
import matchle.exception.CorpusException.EmptyCorpusException;

public final class Corpus implements Iterable<NGram>, Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Set<NGram> corpus;
    private final transient Map<NGram, Map<NGram, Long>> scoreCache;
    private final transient Map<NGram, Long> worstCaseCache;
    private final transient Map<NGram, Double> averageCaseCache;

    private Corpus(Set<NGram> corpus) {
        this.corpus = Set.copyOf(corpus);
        this.scoreCache = new ConcurrentHashMap<>();
        this.worstCaseCache = new ConcurrentHashMap<>();
        this.averageCaseCache = new ConcurrentHashMap<>();
    }

    public Set<NGram> corpus() {
        return corpus;
    }

    public int wordSize() {
        return corpus.isEmpty() ? 0 : corpus.iterator().next().size();
    }

    public boolean contains(NGram ngram) {
        return corpus.contains(ngram);
    }

    public int size() {
        return corpus.size();
    }

    public long size(Filter filter) {
        return corpus.stream().filter(filter::test).count();
    }

    @Override
    public Iterator<NGram> iterator() {
        return corpus.iterator();
    }

    // add stream into corpus
    public java.util.stream.Stream<NGram> stream() {
        return corpus.stream();
    }

    // ---------------- new added function for hw4 ----------------

    /**
     * optimized score method, use cache to improve performance
     */
    public long score(NGram key, NGram guess) {
        if (corpus.isEmpty()) {
            throw new EmptyCorpusException();
        }
        
        // use computeIfAbsent in a single operation
        return scoreCache
            .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(guess, g -> {
                Filter filter = NGramMatcher.of(key, g).match();
                return corpus.stream().filter(filter::test).count();
            });
    }
    
    /**
     * optimized scoreWorstCase method, use cache and parallel computation
     */
    public long scoreWorstCase(NGram guess) {
        if (corpus.isEmpty()) {
            throw new EmptyCorpusException();
        }
        
        // check cache
        if (worstCaseCache.containsKey(guess)) {
            return worstCaseCache.get(guess);
        }
        
        // use parallel stream to improve performance
        long worst = corpus.stream()
                .parallel()
                .mapToLong(key -> score(key, guess))
                .max()
                .orElse(0);
                
        // put into cache
        worstCaseCache.put(guess, worst);
        
        return worst;
    }
    
    /**
     * optimized scoreAverageCase method, use cache and parallel computation
     */
    public double scoreAverageCase(NGram guess) {
        if (corpus.isEmpty()) {
            throw new EmptyCorpusException();
        }
        
        // check cache
        if (averageCaseCache.containsKey(guess)) {
            return averageCaseCache.get(guess);
        }
        
        // use parallel stream to improve performance
        double average = corpus.stream()
                .parallel()
                .mapToLong(key -> score(key, guess))
                .average()
                .orElse(0);
                
        // put into cache
        averageCaseCache.put(guess, average);
        
        return average;
    }

    /**
     * a general method to replace the repeated code in bestWorstCaseGuess and bestAverageCaseGuess
     * @param <T> the type of the score
     * @param scoreFunction the function to calculate the score
     * @param comparator the comparator to compare the score
     * @return the best guess
     */
    private <T extends Comparable<T>> NGram findBestGuess(
            Function<NGram, T> scoreFunction, 
            T initialBestScore) {
        
        if (corpus.isEmpty()) {
            throw new EmptyCorpusException();
        }
        
        NGram best = null;
        T bestScore = initialBestScore;
        
        for (NGram guess : corpus) {
            T score = scoreFunction.apply(guess);
            if (best == null || score.compareTo(bestScore) < 0) {
                bestScore = score;
                best = guess;
            }
        }
        return best;
    }

    /**
     * return the best worst-case guess
     */
    public NGram bestWorstCaseGuess() {
        return findBestGuess(this::scoreWorstCase, Long.MAX_VALUE);
    }

    /**
     * return the best average-case guess
     */
    public NGram bestAverageCaseGuess() {
        return findBestGuess(this::scoreAverageCase, Double.MAX_VALUE);
    }

    /**
     * return best guess based on special rules, a toLongFunction
     * compute a long for each n-gram
     * choose smallest n-gram score
     */
    
    public NGram bestGuess(ToLongFunction<NGram> criterion) {
        if (corpus.isEmpty()) {
            throw new EmptyCorpusException();
        }
        NGram best = null;
        long bestScore = Long.MAX_VALUE;
        for (NGram guess : corpus) {
            long score = criterion.applyAsLong(guess);
            if (score < bestScore) {
                bestScore = score;
                best = guess;
            }
        }
        return best;
    }
    
    // ---------------- Builder inside class ----------------

    public static final class Builder {
        private final Set<NGram> ngrams;

        // use the constructor with parameters
        private Builder(Set<NGram> ngrams) {
            this.ngrams = new HashSet<>(ngrams);
        }
        
        public static final Builder EMPTY = new Builder(new HashSet<>());
        
        public static final Builder of(Corpus corpus) {
            Objects.requireNonNull(corpus, "Corpus cannot be null");
            return new Builder(corpus.corpus());
        }
        
        public static Builder of() {
            return new Builder(new HashSet<>());
        }
        
        public Builder add(NGram ngram) {
            Objects.requireNonNull(ngram, "NGram cannot be null");
            ngrams.add(ngram);
            return this;
        }
        
        public Builder addAll(Collection<NGram> ngramsCollection) {
            Objects.requireNonNull(ngramsCollection, "Collection cannot be null");
            ngrams.addAll(
                ngramsCollection.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet())
            );
            return this;
        }
        
        public Builder filter(Filter filter) {
            return new Builder(ngrams.stream().filter(filter::test).collect(Collectors.toSet()));
        }
        
        public boolean isConsistent(Integer wordSize) {
            return ngrams.stream().allMatch(ngram -> ngram.size() == wordSize);
        }
        
        public Corpus build() {
            if (ngrams.isEmpty()) {
                return null;
            }
            
            if (!isConsistent(wordSize())) {
                throw new CorpusException.InconsistentWordSizeException();
            }
            
            return new Corpus(ngrams);
        }
        
        private int wordSize() {
            return ngrams.isEmpty() ? 0 : ngrams.iterator().next().size();
        }
    }
}
