package sie;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.validate.SieError;
import sie.validate.Validator;

/**
 *
 * @author Håkan Lidén
 */
public class Sie4jTest {

    @Test
    public void test_file_with_missing_accounts() {
        Validator validator = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        List<SieError> errors = validator.getErrors();
        long numberOfErrors = 1;
        SieError.Level level = SieError.Level.FATAL;
        String message = "Account number must not be null or empty";
        assertEquals("Should contain" + numberOfErrors + " error", numberOfErrors, errors.size());
        SieError error = errors.get(0);
        String origin = "Document";
        String tag = "#KONTO";
        assertEquals("Level should be " + level, level, error.getLevel());
        assertEquals("Message should be " + message, message, error.getMessage());
        assertTrue("Error should contain an origin", error.getOrigin().isPresent());
        assertEquals("Origin should be " + origin, origin, error.getOrigin().get());
        assertTrue("Error should contain a tag", error.getTag().isPresent());
        assertEquals("Tag should be " + tag, tag, error.getTag().get());
        System.out.println(error);
    }

    @Test
    public void test_file_with_missing_account_balance() {
        List<SieError> errors = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_balance.SE")).getErrors();
        long numberOfErrors = 1;
        SieError.Level level = SieError.Level.FATAL;
        String message = "Balance is not a number";
        assertEquals("Should contain" + numberOfErrors + " error", numberOfErrors, errors.size());
        SieError error = errors.get(0);
        String origin = "Document";
        String tag = "#KONTO";
        assertEquals("Level should be " + level, level, error.getLevel());
        assertEquals("Message should be " + message, message, error.getMessage());
        assertTrue("Error should contain an origin", error.getOrigin().isPresent());
        assertEquals("Origin should be " + origin, origin, error.getOrigin().get());
        assertTrue("Error should contain a tag", error.getTag().isPresent());
        assertEquals("Tag should be " + tag, tag, error.getTag().get());
        System.out.println(error);
    }
}
