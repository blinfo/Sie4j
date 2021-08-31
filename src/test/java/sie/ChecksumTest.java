package sie;

import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class ChecksumTest {

    private static final Document DOCUMENT = Sie4j.toDocument(ChecksumTest.class.getResourceAsStream("/sample/BLBLOV_SIE1.SE"));

    private static InputStream getStream() {
        return ChecksumTest.class.getResourceAsStream("/sample/BLBLOV_SIE1.SE");
    }

    public ChecksumTest() {
    }

    @Test
    public void testCalculate_String() {
        String input = "Sie4j";
        String expResult = "6F326467746F3131616E45332F364E4577692F5856773D3D";
        String result = Checksum.calculate(input);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalculate_Document() {
        String expResult = "6776705244795A33307931304C634A644D4E557864673D3D";
        assertTrue("Document should have checksum", DOCUMENT.getChecksum().isPresent());
        assertEquals("Document checksum should be " + expResult, expResult, DOCUMENT.getChecksum().get());
        assertEquals("Document checksum should be " + expResult, expResult, Checksum.calculate(DOCUMENT));
        assertEquals("InputStream checksum should be " + expResult, expResult, Checksum.calculate(getStream()));
    }
}
