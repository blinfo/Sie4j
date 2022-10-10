package sie;

import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
        assertEquals("line should contain " + numberOfParts + "parts", numberOfParts, parts.size());
        assertEquals("Part 4 should be " + expectedAmount, expectedAmount, parts.get(4));
        String secondLine = "#OIB 0 1511 {1 \"HOTELL\"} -295370.00";
        String expectedAmount2 = "-295370.00";
        List<String> parts2 = StringUtil.getParts(secondLine);
        assertEquals("Second line should contain " + numberOfParts + "parts", numberOfParts, parts.size());
        assertEquals("Second line part 4 should be " + expectedAmount2, expectedAmount2, parts2.get(4));

    }
}
