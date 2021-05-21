package sie.validate;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.SieException;
import sie.domain.Account;
import sie.domain.AccountingPlan;
import sie.domain.Document;
import sie.validate.model.SieError;

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
            long numberOfErrors = 1;
            String errorMessage = "SRU-kod för konto 1110 saknas";
            SieError.Level level = SieError.Level.INFO;
            String tag = "#SRU";
            String origin = AccountingPlan.class.getSimpleName();
            assertEquals("Should contain " + numberOfErrors + " error", numberOfErrors, errors.size());
            assertEquals("Error message should be " + errorMessage, errorMessage, errors.get(0).getMessage());
            assertEquals("Error level should be " + level, level, errors.get(0).getLevel());
            assertTrue("Error should have a tag", errors.get(0).getTag().isPresent());
            assertEquals("Error tag should be " + tag, tag, errors.get(0).getTag().get());
            assertTrue("Error should have an origin", errors.get(0).getOrigin().isPresent());
            assertEquals("Error origin should be " + origin, origin, errors.get(0).getOrigin().get());
            
        });
    }
}
