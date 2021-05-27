package sie.validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.SieException;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_SIE4_file_with_errors() {
        Document document = getDocument("BLBLOV_SIE4_UTF_8_with_errors.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        assertFalse("Logs should not be empty", validator.getLogs().isEmpty());
        assertEquals("Should contain 25 warnings", 25l, validator.getWarnings().size());
    }

    @Test
    public void test_SIE2_file_with_errors() {
        Document document = getDocument("BLBLOV_SIE2_UTF_8_with_errors.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        assertFalse("Logs should not be empty", validator.getLogs().isEmpty());
        assertEquals("Should contain 1 warnings", 1l, validator.getWarnings().size());
    }

    @Test
    public void test_validator_from_SieException() {
        String message = "Taggen får inte förekomma i SIE-formatet.";
        String tag = "#TEST";
        Class origin = Document.class;
        SieLog.Level level = SieLog.Level.CRITICAL;
        DocumentValidator validator = DocumentValidator.of(new SieException(message, tag), origin);
        assertEquals("Validator should contain 1 log", 1l, validator.getLogs().size());
        assertEquals("Validator should contain 1 critical error", 1l, validator.getCriticalErrors().size());
        SieLog critical = validator.getCriticalErrors().get(0);
        assertEquals("Message should be " + message, message, critical.getMessage());
        assertEquals("Level should be " + level, level, critical.getLevel());
        assertTrue("Log should contain origin", critical.getOrigin().isPresent());
        assertEquals("Origin should be " + origin.getSimpleName(), origin.getSimpleName(), critical.getOrigin().get());
        assertTrue("Log should contain tag", critical.getTag().isPresent());
        assertEquals("Tag should be " + tag, tag, critical.getTag().get());
    }

    @Test
    public void test_balances_and_results_against_vouchers() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        long expectedNumberOfWarnings = 82;
        String expectedFirstMessage = "Resultat för konto 3001 år 0 stämmer inte med summering av verifikationerna. Resultat: -25035.36 Summa: 0.00";
        assertTrue("Log list should not be empty", validator.getLogs().size() > 0);
        assertTrue("Validator should show imbalance", validator.hasResultBalanceVsVoucherImbalance());
        assertEquals("Validator should contain " + expectedNumberOfWarnings + " warnings", expectedNumberOfWarnings, validator.getWarnings().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, validator.getWarnings().get(0).getMessage());
    }
}
