package sie.validate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class SieLog implements Comparable<SieLog> {

    private final String origin;
    private final Level level;
    private final String tag;
    private final String message;
    private final String line;

    private SieLog(String origin, Level level, String tag, String message, String line) {
        this.origin = origin;
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.line = line;
    }

    public static SieLog of(Class origin, SieException exception) {
        return of(origin, exception, null);
    }

    public static SieLog of(Class origin, SieException exception, String line) {
        SieLog.Builder builder = builder()
                .level(Level.CRITICAL)
                .origin(origin)
                .message(exception.getLocalizedMessage())
                .line(line);
        exception.getTag().ifPresent(builder::tag);
        return builder.build();
    }

    public static SieLog info(Class origin, String message, String tag, String line) {
        return builder().origin(origin).message(message).tag(tag).level(Level.INFO).line(line).build();
    }

    public static SieLog warning(Class origin, String message) {
        return builder().origin(origin).message(message).level(Level.WARNING).build();
    }
    public static SieLog warning(Class origin, String message, String tag, String line) {
        return builder().origin(origin).message(message).tag(tag).level(Level.WARNING).line(line).build();
    }

    public static SieLog critical(Class origin, String message, String tag, String line) {
        return builder().origin(origin).message(message).tag(tag).level(Level.INFO).line(line).build();
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
        return Stream.of(message.split("\n")).filter(l -> !l.startsWith(" #")).collect(Collectors.joining("\n"));
    }

    public Optional<String> getLine() {
        if (line != null) {
            return Optional.of(line);
        }
        return Stream.of(message.split("\n")).filter(l -> l.startsWith(" #")).map(l -> l.substring(1)).findFirst();
    }

    @Override
    public String toString() {
        return "SieLog{" + "origin=" + origin + ", level=" + level + ", tag=" + tag + ", message=" + message + '}';
    }

    @Override
    public int compareTo(SieLog other) {
        int result = other.getLevel().compareTo(getLevel());
        if (result == 0) {
            result = getTag().orElse("").compareTo(other.getTag().orElse(""));
        }
        if (result == 0) {
            result = message.compareTo(other.message);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.origin);
        hash = 37 * hash + Objects.hashCode(this.level);
        hash = 37 * hash + Objects.hashCode(this.tag);
        hash = 37 * hash + Objects.hashCode(this.message);
        hash = 37 * hash + Objects.hashCode(this.line);
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
        if (!Objects.equals(this.line, other.line)) {
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
        private String line;

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

        public Builder line(String line) {
            this.line = line;
            return this;
        }

        public SieLog build() {
            return new SieLog(origin, level, tag, message, line);
        }
    }
}
