package sie.validate;

import java.util.Optional;

/**
 *
 * @author Håkan Lidén
 */
public class SieError implements Comparable<SieError> {

    private final String origin;
    private final Level level;
    private final String tag;
    private final String message;

    private SieError(String origin, Level level, String tag, String message) {
        this.origin = origin;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    static SieError.Builder builder() {
        return new Builder();
    }

    public Optional<String> getOrigin() {
        return Optional.ofNullable(origin);
    }

    public Level getLevel() {
        return Optional.ofNullable(level).orElse(Level.DEFAULT);
    }

    public Optional<String> getTag() {
        return Optional.ofNullable(tag);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SieError{" + "origin=" + origin + ", level=" + level + ", tag=" + tag + ", message=" + message + '}';
    }

    @Override
    public int compareTo(SieError other) {
        int result = other.level.compareTo(level);
        if (result == 0) {
            result = tag.compareTo(other.tag);
        }
        if (result == 0) {
            result = message.compareTo(other.message);
        }
        return result;
    }

    public enum Level {
        INFO, WARNING, FATAL;
        public static final Level DEFAULT = INFO;
    }

    static class Builder {

        private String origin;
        private Level level;
        private String tag;
        private String message;

        private Builder() {
        }

        public Builder origin(Class origin) {
            this.origin = origin.getSimpleName();
            return this;
        }

        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder level(Level level) {
            this.level = level;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public SieError build() {
            return new SieError(origin, level, tag, message);
        }
    }
}
