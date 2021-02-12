package sie;

import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 *
 */
public class SieParserTest {

    @Test
    public void test_StreamReader_read() {
        String expectedProgramLine = "#PROGRAM \"BL Administration\" 2018.2.101";
        String expectedCompanyNameLine = "#FNAMN \"Övningsföretaget AB\"";
        String content = SieParser.read(Helper.getSIE(4, 'E'));
        assertTrue("Should contain " + expectedProgramLine, content.contains(expectedProgramLine));
        assertTrue("Should contain " + expectedCompanyNameLine, content.contains(expectedCompanyNameLine));
    }

    @Test
    public void test_StreamReader_encoding_handling() {
        String cp437content = SieParser.read(getStream(""));
        String utf8content = SieParser.read(getStream("_UTF_8"));
        String iso8859content = SieParser.read(getStream("_ISO_8859_15"));
        assertEquals("Content should be same", cp437content, utf8content);
        assertEquals("Content should be same", cp437content, iso8859content);
    }

    @Test
    public void test_checkSumForDocument() {
        String expectedChecksum = "B46D053906DE60431B15CA0F5DD18353";
        Document cp437doc = Sie4j.toDocument(getStream(""));
        Document utf8doc = Sie4j.toDocument(getStream("_UTF_8"));
        Document iso8859doc = Sie4j.toDocument(getStream("_ISO_8859_15"));
        assertTrue("Document checksum should exist", cp437doc.getChecksum().isPresent());
        assertTrue("Document checksum should exist", utf8doc.getChecksum().isPresent());
        assertTrue("Document checksum should exist", iso8859doc.getChecksum().isPresent());
        assertEquals("Checksum should equal " + expectedChecksum, expectedChecksum, cp437doc.getChecksum().get());
        assertEquals("Checksum should be same", cp437doc.getChecksum().get(), utf8doc.getChecksum().get());
        assertEquals("Checksum should be same", cp437doc.getChecksum().get(), iso8859doc.getChecksum().get());

    }

    private InputStream getStream(String string) {
        String path = "/sample/BLBLOV_SIE4";
        String suffix = ".SI";
        return getClass().getResourceAsStream(path + string + suffix);
    }
}