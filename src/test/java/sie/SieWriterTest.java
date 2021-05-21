package sie;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.Document;
import sie.sample.SampleDocumentGenerator;
import sie.sample.Version;

/**
 *
 * @author Håkan Lidén
 */
public class SieWriterTest {
    private static final Document SAMPLE_DOC = SampleDocumentGenerator.generate();

    @Test
    public void test_SieWriter_fromDocument() {
        String expectedVersion = Version.current().toString();
        String expectedName = SAMPLE_DOC.getMetaData().getCompany().getName();
        String sieString = SieWriter.write(SAMPLE_DOC);
        assertTrue("String should contain #FORMAT PC8", sieString.contains("PC8"));
        assertTrue("String should contain #PROGRAM \"Sie4j\" \"" + expectedVersion + "\"", sieString.contains("#PROGRAM \"Sie4j\" \"" + expectedVersion + "\""));
        assertTrue("String should contain #FNAMN \"" + expectedName + "\"", sieString.contains("#FNAMN \"" + expectedName + "\""));
        assertTrue("String should contain #RAR 0", sieString.contains("#RAR 0"));
        assertTrue("String should contain #TAXAR ", sieString.contains("#TAXAR "));
        assertTrue("String should contain #KPTYP ", sieString.contains("#KPTYP "));
        assertTrue("String should contain #KONTO ", sieString.contains("#KONTO "));
        assertTrue("String should contain #VER ", sieString.contains("#VER "));
        assertTrue("String should contain #TRANS ", sieString.contains("#TRANS "));
    }
}
