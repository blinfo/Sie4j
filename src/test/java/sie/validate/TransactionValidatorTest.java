package sie.validate;

import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.domain.*;

/**
 *
 * @author Håkan Lidén
 */
public class TransactionValidatorTest {

    @Test
    public void test_transaction_with_missing_account_number() {
        Transaction trans = Transaction.builder().amount(BigDecimal.ONE).apply();
        TransactionValidator validator = TransactionValidator.of(trans, Document.Type.E4);
        assertEquals("Should contain 1 log", 1l, validator.getLogs().size());
        assertEquals("Should contain 1 fatal log", 1l, validator.getCriticalErrors().size());
    }

    @Test
    public void test_transaction_with_missing_account_number_and_amount() {
        Transaction trans = Transaction.builder().apply();
        TransactionValidator validator = TransactionValidator.of(trans, Document.Type.E4);
        assertEquals("Should contain 2 logs", 2l, validator.getLogs().size());
        assertEquals("Should contain 2 fatal logs", 2l, validator.getCriticalErrors().size());
    }
}
