package sie.validate;

import sie.log.SieLog;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.domain.*;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class AccountingPlanValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_accountingPlan_with_missing_account_numbers() {
        String expectedMessage = "Kontonummer saknas";
        SieException exeption = assertThrows(SieException.class, () -> getDocument("BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        assertEquals(expectedMessage, exeption.getMessage());
    }

    @Test
    public void test_accountingPlan_sie2() {
        Document doc = getDocument("BLBLOV_SIE2_UTF_8_with_multiple_errors.SE");
        doc.optAccountingPlan().ifPresent(plan -> {
            List<SieLog> logs = AccountingPlanValidator.of(plan, doc.metaData().sieType()).getLogs();
            long numberOfLogs = 1;
            String logMessage2 = "SRU-kod för konto 1110 saknas";
            SieLog.Level level2 = SieLog.Level.INFO;
            String tag2 = "#SRU";
            String origin2 = AccountingPlan.class.getSimpleName();
            SieLog log2 = logs.get(0);
            assertEquals(numberOfLogs, logs.size());
            assertEquals(logMessage2, log2.getMessage());
            assertEquals(level2, log2.getLevel());
            assertTrue(log2.getTag().isPresent());
            assertEquals(tag2, log2.getTag().get());
            assertTrue(log2.getOrigin().isPresent());
            assertEquals(origin2, log2.getOrigin().get());
        });
    }
}
