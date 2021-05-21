package sie.sample;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class SampleDocumentGeneratorTest {

    @Test
    public void test_generate() {
        Version current = Version.current();
        Document doc = SampleDocumentGenerator.generate();
        assertEquals("Version should be " + current.toString(), current.toString(), doc.getMetaData().getProgram().getVersion());
        assertEquals("Program name should be " + "Sie4j", "Sie4j", doc.getMetaData().getProgram().getName());
    }
}
