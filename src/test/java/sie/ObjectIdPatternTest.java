package sie;

import java.util.regex.Matcher;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Håkan Lidén
 */
public class ObjectIdPatternTest {

    @Test
    public void test_object_with_StringId() {
        String line1 = "1 \"HOTELL\"";
        Matcher matcher1 = DocumentFactory.OBJECT_ID_PATTERN.matcher(line1);
        assertTrue(matcher1.matches());
        String line2 = "1 \"300\"";
        Matcher matcher2 = DocumentFactory.OBJECT_ID_PATTERN.matcher(line2);
        assertTrue(matcher2.matches());
        String line3 = "1 300";
        Matcher matcher3 = DocumentFactory.OBJECT_ID_PATTERN.matcher(line3);
        assertTrue(matcher3.matches());
    }
}
