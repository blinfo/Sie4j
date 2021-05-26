package sie.validate;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.SieException;
import sie.domain.AccountingPlan;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class AccountingPlanValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_accountingPlan_with_missing_account_numbers() {
        String expectedMessage = "Kontonummer får inte vara null eller tom sträng";
        SieException exeption = assertThrows("", SieException.class, () -> getDocument("BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        assertEquals("Exception message should be " + expectedMessage, expectedMessage, exeption.getMessage());
    }

    @Test
    public void test_accountingPlan() {
        Document doc = getDocument("BLBLOV_SIE4_UTF_8_with_errors.SE");
        Document.Type type = doc.getMetaData().getSieType();
        doc.getAccountingPlan().ifPresent(plan -> {
            List<SieLog> logs = AccountingPlanValidator.of(plan, doc.getMetaData().getSieType()).getLogs();
            long numberOfLogs = 2;
            String logMessage1 = "Kontot har inte ett numeriskt värde: 11AF";
            SieLog.Level level1 = SieLog.Level.WARNING;
            String tag1 = "#KONTO";
            String origin1 = AccountingPlan.class.getSimpleName();
            SieLog log1 = logs.get(0);
            assertEquals("Should contain " + numberOfLogs + " logs", numberOfLogs, logs.size());
            assertEquals("Log message should be " + logMessage1, logMessage1, log1.getMessage());
            assertEquals("Log level should be " + level1, level1, log1.getLevel());
            assertTrue("Log should have a tag", log1.getTag().isPresent());
            assertEquals("Log tag should be " + tag1, tag1, log1.getTag().get());
            assertTrue("Log should have an origin", log1.getOrigin().isPresent());
            assertEquals("Log origin should be " + origin1, origin1, log1.getOrigin().get());
            
            String logMessage2 = "SRU-kod för konto 1110 saknas";
            SieLog.Level level2 = SieLog.Level.INFO;
            String tag2 = "#SRU";
            String origin2 = AccountingPlan.class.getSimpleName();
            SieLog log2 = logs.get(1);
            assertEquals("Should contain " + numberOfLogs + " logs", numberOfLogs, logs.size());
            assertEquals("Log message should be " + logMessage2, logMessage2, log2.getMessage());
            assertEquals("Log level should be " + level2, level2, log2.getLevel());
            assertTrue("Log should have a tag", log2.getTag().isPresent());
            assertEquals("Log tag should be " + tag2, tag2, log2.getTag().get());
            assertTrue("Log should have an origin", log2.getOrigin().isPresent());
            assertEquals("Log origin should be " + origin2, origin2, log2.getOrigin().get());
            
        });
    }
}
