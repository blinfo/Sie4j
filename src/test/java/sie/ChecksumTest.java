package sie;

import org.junit.Test;
import static org.junit.Assert.*;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class ChecksumTest {

    private static final Document DOCUMENT = Sie4j.toDocument(ChecksumTest.class.getResourceAsStream("/sample/BLBLOV_SIE1.SE"));

    private static byte[] getStream() {
        return SieReader.streamToByteArray(ChecksumTest.class.getResourceAsStream("/sample/BLBLOV_SIE1.SE"));
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
        String expResult = "344D614846547151326F2F466962684E6C6E683179413D3D";
        assertTrue("Document should have checksum", DOCUMENT.getChecksum().isPresent());
        assertEquals("Document checksum should be " + expResult, expResult, DOCUMENT.getChecksum().get());
        assertEquals("Document checksum should be " + expResult, expResult, Checksum.calculate(DOCUMENT));
        assertEquals("InputStream checksum should be " + expResult, expResult, Checksum.calculate(getStream()));
    }
}
