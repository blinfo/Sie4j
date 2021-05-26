package sie.validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_file_with_errors() {
        Document document = getDocument("BLBLOV_SIE4_UTF_8_with_errors.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        assertFalse("Logs should not be empty", validator.getLogs().isEmpty());
        assertEquals("Should contain 1 info", 1l, validator.getInfo().size());
        assertEquals("Should contain 1 warnings", 1l, validator.getWarnings().size());
        assertEquals("Should contain 25 critical errors", 25l, validator.getCriticalErrors().size());
    }
}
