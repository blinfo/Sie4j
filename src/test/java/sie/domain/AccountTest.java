package sie.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import sie.exception.*;

/**
 *
 * @author Håkan Lidén
 */
public class AccountTest {

    @Test
    public void test_account_with_empty_number_throws_exception() {
        String expectedMessage = "Kontonummer saknas";
        String number = "";
        SieException ex = assertThrows("Builder should throw exception", MissingAccountNumberException.class, () -> Account.builder(number).apply());
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_account_with_null_number_throws_exception() {
        String expectedMessage = "Kontonummer saknas";
        String number = null;
        SieException ex = assertThrows("Builder should throw exception", MissingAccountNumberException.class, () -> Account.builder(number).apply());
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }
}
