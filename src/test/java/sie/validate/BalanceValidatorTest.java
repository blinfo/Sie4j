package sie.validate;

import static org.junit.Assert.*;
import org.junit.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class BalanceValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_balances_and_results_against_vouchers() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        BalanceValidator validator = BalanceValidator.from(document);
        long expectedNumberOfLogs = 62;
        String expectedFirstMessage = "Resultat för konto 3001 år 0 stämmer inte med summering av verifikationerna\n"
                + " Resultat: -25035.36 Summa: 0.00";
        String expectedFirstLine = "#RES 0 3001 -25035.36";
        assertFalse("Log list should not be empty", validator.getLogs().isEmpty());
        assertEquals("Validator should contain " + expectedNumberOfLogs + " logs", expectedNumberOfLogs, validator.getLogs().size());
        assertEquals("Validator should contain " + expectedNumberOfLogs + " warnings", expectedNumberOfLogs, validator.getWarnings().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, validator.getLogs().get(0).getMessage());
        assertTrue("First line should be present", validator.getLogs().get(0).getLine().isPresent()); 
        assertEquals("First line should be " + expectedFirstLine, expectedFirstLine, validator.getLogs().get(0).getLine().get());
    }
}
