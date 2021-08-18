package sie.validate;

import java.util.Objects;
import java.util.Optional;
import sie.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class SieLog implements Comparable<SieLog> {

    private final String origin;
    private final Level level;
    private final String tag;
    private final String message;

    private SieLog(String origin, Level level, String tag, String message) {
        this.origin = origin;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    public static SieLog of(Class origin, SieException exception) {
        SieLog.Builder builder = builder()
                .level(Level.CRITICAL)
                .origin(origin)
                .message(exception.getLocalizedMessage());
        exception.getTag().ifPresent(builder::tag);
        return builder.build();
    }

    public static SieLog info(Class origin, String message) {
        return info(origin, message, null);
    }

    public static SieLog info(Class origin, String message, String tag) {
        return builder().origin(origin).message(message).tag(tag).level(Level.INFO).build();
    }

    public static SieLog warning(Class origin, String message) {
        return warning(origin, message, null);
    }

    public static SieLog warning(Class origin, String message, String tag) {
        return builder().origin(origin).message(message).tag(tag).level(Level.WARNING).build();
    }

    public static SieLog critical(Class origin, String message) {
        return critical(origin, message, null);
    }

    public static SieLog critical(Class origin, String message, String tag) {
        return builder().origin(origin).message(message).tag(tag).level(Level.INFO).build();
    }

    static SieLog.Builder builder() {
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
        return "SieLog{" + "origin=" + origin + ", level=" + level + ", tag=" + tag + ", message=" + message + '}';
    }

    @Override
    public int compareTo(SieLog other) {
        int result = other.level.compareTo(level);
        if (result == 0) {
            result = tag.compareTo(other.tag);
        }
        if (result == 0) {
            result = message.compareTo(other.message);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.origin);
        hash = 17 * hash + Objects.hashCode(this.level);
        hash = 17 * hash + Objects.hashCode(this.tag);
        hash = 17 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SieLog other = (SieLog) obj;
        if (!Objects.equals(this.origin, other.origin)) {
            return false;
        }
        if (!Objects.equals(this.tag, other.tag)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return this.level == other.level;
    }

    public enum Level {
        INFO, WARNING, CRITICAL;
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

        public SieLog build() {
            return new SieLog(origin, level, tag, message);
        }
    }
}
