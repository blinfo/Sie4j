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
        String expectedMessage = "Account number must not be null or empty";
        SieException exeption = assertThrows("", SieException.class, () -> getDocument("BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        assertEquals("Exception message should be " + expectedMessage, expectedMessage, exeption.getMessage());
    }

    @Test
    public void test_accountingPlan() {
        Document doc = getDocument("BLBLOV_SIE4_UTF_8_with_errors.SE");
        Document.Type type = doc.getMetaData().getSieType();
        doc.getAccountingPlan().ifPresent(plan -> {
            List<SieError> errors = AccountingPlanValidator.of(plan, doc.getMetaData().getSieType()).getErrors();
            long numberOfErrors = 2;
            String errorMessage1 = "Kontot har inte ett numeriskt värde: 11AF";
            SieError.Level level1 = SieError.Level.WARNING;
            String tag1 = "#KONTO";
            String origin1 = AccountingPlan.class.getSimpleName();
            SieError error1 = errors.get(0);
            assertEquals("Should contain " + numberOfErrors + " error", numberOfErrors, errors.size());
            assertEquals("Error message should be " + errorMessage1, errorMessage1, error1.getMessage());
            assertEquals("Error level should be " + level1, level1, error1.getLevel());
            assertTrue("Error should have a tag", error1.getTag().isPresent());
            assertEquals("Error tag should be " + tag1, tag1, error1.getTag().get());
            assertTrue("Error should have an origin", error1.getOrigin().isPresent());
            assertEquals("Error origin should be " + origin1, origin1, error1.getOrigin().get());
            
            String errorMessage2 = "SRU-kod för konto 1110 saknas";
            SieError.Level level2 = SieError.Level.INFO;
            String tag2 = "#SRU";
            String origin2 = AccountingPlan.class.getSimpleName();
            SieError error2 = errors.get(1);
            assertEquals("Should contain " + numberOfErrors + " error", numberOfErrors, errors.size());
            assertEquals("Error message should be " + errorMessage2, errorMessage2, error2.getMessage());
            assertEquals("Error level should be " + level2, level2, error2.getLevel());
            assertTrue("Error should have a tag", error2.getTag().isPresent());
            assertEquals("Error tag should be " + tag2, tag2, error2.getTag().get());
            assertTrue("Error should have an origin", error2.getOrigin().isPresent());
            assertEquals("Error origin should be " + origin2, origin2, error2.getOrigin().get());
            
        });
    }
}
