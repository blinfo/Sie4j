package sie.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class VersionTest {

    @Test
    public void test_non_semantic_version_number_throws_exception() {
        String input = "Alpha";
        String expectedMessage = "Could not construct a Version object from string: " + input;
        SieException ex = assertThrows("", SieException.class, () -> Version.from(input));
        assertEquals("Expected message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_incomplete_version_number_throws_exception() {
        String input = "1";
        String expectedMessage = "Could not construct a Version object from string: " + input;
        SieException ex = assertThrows("", SieException.class, () -> Version.from(input));
        assertEquals("Expected message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_semantic_version_with_only_two_version_parts() {
        String input = "1.0";
        String expectedVersionString = "1.0.0";
        Version version = Version.from(input);
        assertEquals("Expected version string should be " + expectedVersionString, expectedVersionString, version.toString());
    }

    @Test
    public void test_semantic_version_with_three_version_parts() {
        String input = "2.0.14";
        String expectedVersionString = input;
        Version version = Version.from(input);
        assertEquals("Expected version string should be " + expectedVersionString, expectedVersionString, version.toString());
    }

    @Test
    public void test_semantic_version_of_two_version_numbers() {
        Integer major = 1;
        Integer minor = 13;
        String expectedVersionString = major + "." + minor + ".0";
        Version version = Version.of(major, minor);
        assertEquals("Expected version string should be " + expectedVersionString, expectedVersionString, version.toString());
    }

    @Test
    public void test_semantic_version_of_three_version_numbers() {
        Integer major = 1;
        Integer minor = 13;
        Integer patch = 3;
        String expectedVersionString = major + "." + minor + "." + patch;
        Version version = Version.of(major, minor, patch);
        assertEquals("Expected version string should be " + expectedVersionString, expectedVersionString, version.toString());
    }

    @Test
    public void test_currentVersion() {
        // Check that no exception is thrown of a version created from the content in the pom.xml file
        Version version = Version.current();
        assertTrue("Current version number should match pattern", Version.PATTERN.matcher(version.toString()).matches());
    }
}
