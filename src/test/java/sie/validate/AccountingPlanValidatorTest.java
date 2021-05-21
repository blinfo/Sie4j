package sie.validate;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import sie.SieException;
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
        Arrays.asList("CC2-foretaget.SE", "BLBLOV_SIE4_UTF_8_with_errors.SE").forEach(file -> {
            System.out.println("File: " + file);
            Document doc = getDocument(file);
            Document.Type type = doc.getMetaData().getSieType();
            doc.getAccountingPlan().ifPresent(plan -> {
                AccountingPlanValidator.of(plan, doc.getMetaData().getSieType()).getErrors().forEach(System.out::println);
                System.out.println("");
            });
        });

    }
}
