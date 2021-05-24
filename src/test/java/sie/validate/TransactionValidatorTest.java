package sie.validate;

import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.domain.Document;
import sie.domain.Transaction;

/**
 *
 * @author Håkan Lidén
 */
public class TransactionValidatorTest {

    @Test
    public void test_transaction_with_missing_account_number() {
        Transaction trans = Transaction.builder().amount(BigDecimal.ONE).apply();
        TransactionValidator validator = TransactionValidator.of(trans, Document.Type.E4);
        assertEquals("Should contain 1 error", 1l, validator.getErrors().size());
        assertEquals("Should contain 1 fatal error", 1l, validator.getFatalErrors().size());
    }

    @Test
    public void test_transaction_with_missing_account_number_and_amount() {
        Transaction trans = Transaction.builder().apply();
        TransactionValidator validator = TransactionValidator.of(trans, Document.Type.E4);
        assertEquals("Should contain 2 error", 2l, validator.getErrors().size());
        assertEquals("Should contain 2 fatal error", 2l, validator.getFatalErrors().size());
    }
}
