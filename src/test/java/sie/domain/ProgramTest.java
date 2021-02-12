package sie.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.Helper;

/**
 *
 * @author Håkan Lidén
 *
 */
public class ProgramTest extends Helper {

    @Test
    public void test_Program_getName() {
        Program program = getDocument(4, 'E').getMetaData().getProgram();
        String expectedResult = "BL Administration";
        assertEquals("Program name should be " + expectedResult, expectedResult, program.getName());
    }

    @Test
    public void test_Program_getVersion() {
        Program program = getDocument(4, 'E').getMetaData().getProgram();
        String expectedResult = "2018.2.101";
        assertEquals("Program version should be " + expectedResult, expectedResult, program.getVersion());
    }
}
