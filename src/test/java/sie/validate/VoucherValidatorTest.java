package sie.validate;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.domain.*;

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
        String expectedMessage = "Verifikationen saknar transaktionsrader. ";
        String expectedLine = "#VER \"A\" 2 20170101 \"Representation måltid med alkohol\" 20170119 \"#6 Linda Henriksson\"";
        assertEquals("Log list should contain one log", 1l, result.getLogs().size());
        assertEquals("Log message should be " + expectedMessage, expectedMessage, result.getLogs().get(0).getMessage());
        assertEquals("Log line should be " + expectedLine, expectedLine, result.getLogs().get(0).getLine().orElse(""));
    }
}
