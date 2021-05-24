package sie.validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        long expectedNumberOfErrors = 82;
        System.out.println(validator.getErrors().get(0).getMessage());
        String expectedFirstMessage = "Resultat för konto 3001 år 0 stämmer inte med summering av verifikationerna. Resultat: -25035.36 Summa: 0.00";
        assertTrue("Error list should not be empty", validator.getErrors().size() > 0);
        assertEquals("Validator should contain " + expectedNumberOfErrors + " errors", expectedNumberOfErrors, validator.getErrors().size());
        assertEquals("Validator should contain " + expectedNumberOfErrors + " fatal errors", expectedNumberOfErrors, validator.getFatalErrors().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, validator.getErrors().get(0).getMessage());
    }
}
