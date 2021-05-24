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
    public void test_balances_against_vouchers() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        BalanceValidator validator = BalanceValidator.from(document);
        String expectedFirstMessage = "Utgående balans för konto 1110 år 0 stämmer inte med summering av verifikationerna. Balans: 200.00 Summa: 0.00";
        assertTrue("Error list should not be empty", validator.getErrors().size() > 0);
        assertEquals("Validator should contain 40 errors", 40l, validator.getErrors().size());
        assertEquals("Validator should contain 40 fatal errors", 40l, validator.getFatalErrors().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, validator.getErrors().get(0).getMessage());
    }
}
