package sie.validate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
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
        String expectedFirstMessage = """
                                      Resultat f\u00f6r konto 3001 \u00e5r 0 st\u00e4mmer inte med summering av verifikationerna
                                       Resultat: -25035.36 Summa: 0.00""";
        String expectedFirstLine = "#RES 0 3001 -25035.36";
        assertFalse(validator.getLogs().isEmpty());
        assertEquals(expectedNumberOfLogs, validator.getLogs().size());
        assertEquals(expectedNumberOfLogs, validator.getWarnings().size());
        assertEquals(expectedFirstMessage, validator.getLogs().get(0).getMessage());
        assertTrue(validator.getLogs().get(0).getLine().isPresent()); 
        assertEquals(expectedFirstLine, validator.getLogs().get(0).getLine().get());
    }
}
