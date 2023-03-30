package sie.domain;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class Program implements Entity {

    private final String line;
    private final String name;
    private final String version;

    private Program(String line, String name, String version) {
        this.line = line;
        this.name = name;
        this.version = version;
    }

    public static Program of(String name, String version) {
        return new Program(null, name, version);
    }

    public static Program of(String line, String name, String version) {
        return new Program(line, name, version);
    }

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public String name() {
        return name;
    }

    public String version() {
        return version;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.version);
        return hash;
    }

    /**
     * equals ignores "line".
     *
     * @param obj
     * @return
     */
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
        final Program other = (Program) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.version, other.version);
    }

    @Override
    public String toString() {
        return "Program{" + "line=" + line + ", name=" + name + ", version=" + version + '}';
    }
}
