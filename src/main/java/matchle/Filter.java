package matchle;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Filter implements Predicate<NGram>, Serializable {
    private static final long serialVersionUID = 1L;
    // Since Predicate might not be Serializable, we use transient modifier
    private transient Predicate<NGram> predicate;
    private String pattern;

    private Filter(Predicate<NGram> predicate) {
        this.predicate = predicate;
        this.pattern = "";
    }

    public Filter(Predicate<NGram> predicate, String pattern) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        this.predicate = predicate;
        this.pattern = pattern;
    }
    
    public static Filter from(Predicate<NGram> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return new Filter(predicate);
    }

    public Filter withPattern(String pattern) {
        return new Filter(this.predicate, pattern);
    }

    @Override
    public boolean test(NGram ngram) {
        // If predicate is null (possibly due to deserialization), we return true
        return predicate == null || predicate.test(ngram);
    }

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

    public static final Filter FALSE = new Filter(ngram -> false);

    @Override
    public String toString() {
        return pattern.isEmpty() ? "Filter[]" : pattern;
    }
}