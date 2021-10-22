package sie.validate;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.domain.Document;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
public class VoucherValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_type4E_with_empty_voucher() {
        Document document = getDocument("BLBLOV_SIE4_UTF_8_with_empty_voucher.SE");
        Document.Type type = document.getMetaData().getSieType();
        Voucher voucher = document.getVouchers().get(1);
        VoucherValidator result = VoucherValidator.of(voucher, type);
        String expectedMessage = "Verifikationen innehåller inga transaktionsrader Serie: A Nummer: 2";
        assertEquals("Log list should contain one log", 1l, result.getLogs().size());
        assertEquals("Log message should be " + expectedMessage, expectedMessage, result.getLogs().get(0).getMessage());
    }
}
