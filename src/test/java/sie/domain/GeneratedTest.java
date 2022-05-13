package sie.domain;

import java.time.LocalDate;
import static org.junit.Assert.*;
import org.junit.Test;
import sie.Helper;

/**
 *
 * @author Håkan Lidén
 *
 */
public class GeneratedTest extends Helper {

    @Test
    public void test_Generated_getDate() {
        Generated generated = getDocument(4, 'E').getMetaData().getGenerated();
        LocalDate expectedResult = LocalDate.parse("2018-05-07");
        assertEquals("Generated date should be " + expectedResult, expectedResult, generated.getDate());
    }

    @Test
    public void test_Generated_getSignature() {
        Generated generated = getDocument(4, 'E').getMetaData().getGenerated();
        String expectedResult = "1 Linda Henriksson";
        assertTrue("Generated signature should exist", generated.getSignature().isPresent());
        assertEquals("Generated signature should be " + expectedResult, expectedResult, generated.getSignature().get());
    }
}
