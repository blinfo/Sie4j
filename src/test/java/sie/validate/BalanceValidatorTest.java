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
//        Document document = getDocument("Programgruppen AB 20190501-20200430 BokslutsTransaktioner.se");
        BalanceValidator validator = BalanceValidator.from(document);
        validator.getLogs().forEach(System.out::println);
//        
        long expectedNumberOfLogs = 82;
        String expectedFirstMessage = "Resultat för konto 3001 år 0 stämmer inte med summering av verifikationerna. Resultat: -25035.36 Summa: 0.00";
        assertTrue("Log list should not be empty", validator.getLogs().size() > 0);
        assertEquals("Validator should contain " + expectedNumberOfLogs + " logs", expectedNumberOfLogs, validator.getLogs().size());
        assertEquals("Validator should contain " + expectedNumberOfLogs + " warnings", expectedNumberOfLogs, validator.getWarnings().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, validator.getLogs().get(0).getMessage());
    }
}
