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
        String expResult = "A36760B68D756A7137FFA344C22FD757";
        String result = Checksum.calculate(input);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalculate_Document() {
        String expResult = "8980D430088E424B13FE30DE4C995D16";
        expResult = "82FA510F2677D32D742DC25D30D53176";
        assertTrue("Document should have checksum", DOCUMENT.getChecksum().isPresent());
        assertEquals("Document checksum should be " + expResult, expResult,  DOCUMENT.getChecksum().get());
        assertEquals("Document checksum should be " + expResult, expResult,  Checksum.calculate(DOCUMENT));
        assertEquals("InputStream checksum should be " + expResult, expResult,  Checksum.calculate(getStream()));
    }
}
