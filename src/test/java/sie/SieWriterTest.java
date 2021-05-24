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

    @Test
    public void test_SieWriter_fromDocument() {
        Document sampleDoc = SampleDocumentGenerator.generate();
        String expectedVersion = Version.current().toString();
        String expectedName = sampleDoc.getMetaData().getCompany().getName();
        String sieString = SieWriter.write(sampleDoc);
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
