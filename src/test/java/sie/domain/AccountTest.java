package sie.domain;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
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
        SieException ex = assertThrows(MissingAccountNumberException.class, () -> Account.builder(number).apply());
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void test_account_with_null_number_throws_exception() {
        String expectedMessage = "Kontonummer saknas";
        String number = null;
        SieException ex = assertThrows(MissingAccountNumberException.class, () -> Account.builder(number).apply());
        assertEquals(expectedMessage, ex.getMessage());
    }
    
    @Test
    public void test_account_type_find() {
        Optional<Account.Type> optType = Account.Type.find("Asset");
        assertTrue(optType.isPresent());
        assertEquals(Account.Type.T, optType.get());
        assertTrue(Account.Type.find("Skuld").isPresent());
        assertFalse(Account.Type.find("Basset").isPresent());
    }
}
