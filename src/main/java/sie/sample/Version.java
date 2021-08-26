package sie.sample;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sie.exception.SieException;

/**
 * The Version is an object representing a semantic version.
 *
 * <p>
 * For more information on semantic versioning, see
 * <a href="https://semver.org/">Semantic Versioning 2.0.0</a>
 *
 * @author Håkan Lidén
 */
public class Version {

    static final Pattern PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?");
    private final Integer major;
    private final Integer minor;
    private final Integer patch;

    private Version(Integer major, Integer minor, Integer patch) {
        this.major = Objects.requireNonNull(major);
        this.minor = Objects.requireNonNull(minor);
        this.patch = patch == null ? 0 : patch;
    }

    /**
     * Creates a Version object from a String.
     * <p>
     * The string should be of the format d.d or d.d.d where d is an integer,
     * e.g. 1.4 or 1.10.3
     * <p>
     *
     * @param string should be of the format d.d or d.d.d (where d is an
     * integer)
     * @return Version
     */
    public static Version from(String string) {
        Matcher matcher = PATTERN.matcher(string);
        if (matcher.matches()) {
            Integer major = Integer.valueOf(matcher.group(1));
            Integer minor = Integer.valueOf(matcher.group(2));
            Integer patch = matcher.group(4) != null ? Integer.valueOf(matcher.group(4)) : null;
            return new Version(major, minor, patch);
        } else {
            throw new SieException("Could not construct a Version object from string: " + string);
        }
    }

    /**
     * Creates a Version object.
     * <p>
     * Returns a Version with major and minor version set.
     *
     * @param major an integer representing the major version part.
     * @param minor an integer representing the minor version part
     * @return Version
     */
    public static Version of(Integer major, Integer minor) {
        return Version.of(major, minor, null);
    }

    /**
     * Creates a Version object.
     * <p>
     * Returns a Version with major, minor and patch version set.
     *
     * @param major an integer representing the major version part.
     * @param minor an integer representing the minor version part
     * @param patch an integer representing the patch version part.
     * @return
     */
    public static Version of(Integer major, Integer minor, Integer patch) {
        return new Version(major, minor, patch);
    }

    /**
     * Returns the current Sie4j version.
     *
     * @return Version
     */
    public static Version current() {
        String string = InputStreamToString.from(Version.class.getResourceAsStream("/version"));
        return from(string);
    }

    /**
     * Returns the Major version number.
     *
     * @return Integer
     */
    public Integer getMajor() {
        return major;
    }

    /**
     * Returns the Minor version number.
     *
     * @return Integer
     */
    public Integer getMinor() {
        return minor;
    }

    /**
     * Returns the Patch version number.
     *
     * @return Integer
     */
    public Integer getPatch() {
        return patch;
    }

    /**
     * Returns a string representation of the version.
     * <p>
     * The string is of the same format as the expected input string for
     * "Version.of(String string)", i.e. 1.4.0 or 2.1.11
     *
     * @return String (i.e. 1.4.0)
     */
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
