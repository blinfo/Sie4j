package sie;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Håkan Lidén
 */
public class StringUtilTest {

    @Test
    public void handle_objectBalance_correctly() {
        String line = "#OIB 0 2421 {1 \"1\"} -12513.00";
        String expectedAmount = "-12513.00";
        List<String> parts = StringUtil.getParts(line);
        long numberOfParts = 6;
        assertEquals(numberOfParts, parts.size());
        assertEquals(expectedAmount, parts.get(4));
        String secondLine = "#OIB 0 1511 {1 \"HOTELL\"} -295370.00";
        String expectedAmount2 = "-295370.00";
        List<String> parts2 = StringUtil.getParts(secondLine);
        assertEquals(numberOfParts, parts.size());
        assertEquals(expectedAmount2, parts2.get(4));

    }
}
