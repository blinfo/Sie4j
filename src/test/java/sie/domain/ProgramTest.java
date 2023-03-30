package sie.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public class ProgramTest extends Helper {

    @Test
    public void test_Program_getName() {
        Program program = getDocument(4, 'E').metaData().program();
        String expectedResult = "BL Administration";
        assertEquals(expectedResult, program.name());
    }

    @Test
    public void test_Program_getVersion() {
        Program program = getDocument(4, 'E').metaData().program();
        String expectedResult = "2018.2.101";
        assertEquals(expectedResult, program.version());
    }

    @Test
    public void test_Program_getVersion_with_quotes() {
        String expectedResult = "1.0 - \"Oblique Ozelot\" - alpha";
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/Quotes_test.si"));
        Program prog = doc.metaData().program();
        assertEquals(expectedResult, prog.version());
    }
}
