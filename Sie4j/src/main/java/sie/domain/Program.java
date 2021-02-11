package sie.domain;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class Program implements Entity {

    private final String name;
    private final String version;

    private Program(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public static Program of(String name, String version) {
        return new Program(name, version);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Program{" + "name=" + name + ", version=" + version + '}';
    }
}
