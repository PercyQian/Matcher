package matchle;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a filter used to match against NGram objects in the Matchle game.
 * A Filter encapsulates a predicate that determines whether an NGram matches specific criteria,
 * along with a human-readable pattern string that describes the filter.
 * <p>
 * Filters are used to progressively narrow down the candidate words during gameplay
 * by applying constraints based on previous guesses.
 * <p>
 * This class implements both Predicate (for functional operations) and Serializable
 * (to support game state saving).
 */
public class Filter implements Predicate<NGram>, Serializable {
    /** Serialization version UID for consistent serialization across versions */
    private static final long serialVersionUID = 1L;
    
    /**
     * The underlying predicate that performs the actual filtering.
     * Marked as transient since Predicate might not be Serializable.
     */
    private transient Predicate<NGram> predicate;
    
    /**
     * A human-readable string representation of the filter pattern.
     * This describes the filter constraints in a textual format.
     */
    private String pattern;

    /**
     * Creates a new Filter with the specified predicate and an empty pattern.
     *
     * @param predicate The predicate to use for filtering
     */
    private Filter(Predicate<NGram> predicate) {
        this.predicate = predicate;
        this.pattern = "";
    }

    /**
     * Creates a new Filter with the specified predicate and pattern.
     *
     * @param predicate The predicate to use for filtering
     * @param pattern A string representation of the filter pattern
     * @throws NullPointerException if predicate is null
     */
    private Filter(Predicate<NGram> predicate, String pattern) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        this.predicate = predicate;
        this.pattern = pattern;
    }
    
    /**
     * Factory method to create a new Filter with the specified predicate.
     *
     * @param predicate The predicate to use for filtering
     * @return A new Filter instance
     * @throws NullPointerException if predicate is null
     */
    public static Filter from(Predicate<NGram> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return new Filter(predicate);
    }

    /**
     * Factory method to create a new Filter with the specified predicate and pattern.
     *
     * @param predicate The predicate to use for filtering
     * @param pattern A string representation of the filter pattern
     * @return A new Filter instance
     * @throws NullPointerException if predicate is null
     */
    public static Filter from(Predicate<NGram> predicate, String pattern) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return new Filter(predicate, pattern);
    }

    /**
     * Creates a new Filter with the same predicate but a different pattern.
     *
     * @param pattern The new pattern to use
     * @return A new Filter with the updated pattern
     */
    public Filter withPattern(String pattern) {
        return new Filter(this.predicate, pattern);
    }

    /**
     * Tests if the specified NGram satisfies this filter's predicate.
     * If the predicate is null (possibly due to deserialization), the method returns true.
     *
     * @param ngram The NGram to test
     * @return true if the NGram passes the filter, false otherwise
     */
    @Override
    public boolean test(NGram ngram) {
        // If predicate is null (possibly due to deserialization), we return true
        return predicate == null || predicate.test(ngram);
    }

    /**
     * Combines this filter with another optional filter using logical AND.
     * If the other filter is not present, this filter is returned unchanged.
     *
     * @param other An Optional containing another Filter to combine with this one
     * @return A new Filter representing the logical AND of both filters
     */
    public Filter and(Optional<Filter> other) {
        if (!other.isPresent()) {
            return this;
        }
        
        Filter otherFilter = other.get();
        String newPattern = this.pattern;
        
        if (!this.pattern.isEmpty() && !otherFilter.pattern.isEmpty()) {
            newPattern += " AND " + otherFilter.pattern;
        } else if (this.pattern.isEmpty() && !otherFilter.pattern.isEmpty()) {
            newPattern = otherFilter.pattern;
        }
        
        return new Filter(
            ngram -> this.test(ngram) && otherFilter.test(ngram),
            newPattern
        );
    }

    /**
     * Returns a string representation of this filter.
     * If the pattern is empty, returns a generic "Filter[]" string.
     *
     * @return A string representation of this filter
     */
    @Override
    public String toString() {
        return pattern.isEmpty() ? "Filter[]" : pattern;
    }
}