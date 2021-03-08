package sie;

import java.util.regex.Matcher;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Håkan Lidén
 */
public class ObjectIdPatternTest {

    @Test
    public void test_object_with_StringId() {
        String line1 = "1 \"HOTELL\"";
        Matcher matcher1 = DocumentFactory.OBJECT_ID_PATTERN.matcher(line1);
        assertTrue("Pattern should match", matcher1.matches());
        String line2 = "1 \"300\"";
        Matcher matcher2 = DocumentFactory.OBJECT_ID_PATTERN.matcher(line2);
        assertTrue("Pattern should match", matcher2.matches());
        String line3 = "1 300";
        Matcher matcher3 = DocumentFactory.OBJECT_ID_PATTERN.matcher(line3);
        assertTrue("Pattern should match", matcher3.matches());
    }
}
