package matchle;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class Filter implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 由于Predicate可能不是Serializable，我们使用transient修饰
    private final transient Predicate<NGram> predicate;
    private String pattern;

    private Filter(Predicate<NGram> predicate) {
        this.predicate = predicate;
        this.pattern = "";
    }

    public static Filter from(Predicate<NGram> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return new Filter(predicate);
    }

    public boolean test(NGram ngram) {
        // 如果predicate为null（可能是因为反序列化），我们返回true
        // 这样可以保证序列化后的Filter仍然可用
        return predicate != null ? predicate.test(ngram) : true;
    }

    public Filter and(Optional<Filter> other) {
        if (!other.isPresent()) {
            return this;
        }
        
        Filter otherFilter = other.get();
        return new Filter(ngram -> {
            boolean thisResult = this.test(ngram);
            boolean otherResult = otherFilter.test(ngram);
            return thisResult && otherResult;
        });
    }

    public Filter withPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public static final Filter FALSE = new Filter(ngram -> false);

    @Override
    public String toString() {
        return pattern.isEmpty() ? "Filter[]" : pattern;
    }
}