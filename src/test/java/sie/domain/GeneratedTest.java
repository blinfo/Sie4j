package sie.domain;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.Helper;

/**
 *
 * @author Håkan Lidén
 *
 */
public class GeneratedTest extends Helper {

    @Test
    public void test_Generated_getDate() {
        Generated generated = getDocument(4, 'E').metaData().generated();
        LocalDate expectedResult = LocalDate.parse("2018-05-07");
        assertEquals(expectedResult, generated.date());
    }

    @Test
    public void test_Generated_getSignature() {
        Generated generated = getDocument(4, 'E').metaData().generated();
        String expectedResult = "1 Linda Henriksson";
        assertTrue(generated.optSignature().isPresent());
        assertEquals( expectedResult, generated.optSignature().get());
    }
}
