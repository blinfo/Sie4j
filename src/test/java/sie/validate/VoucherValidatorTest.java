package sie.validate;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.Sie4j;
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
        VoucherValidator validator = VoucherValidator.of(voucher, type);
        String expectedMessage = "Verifikationen innehåller inga transaktionsrader. Serie: A. Nummer: 2.";
        assertEquals("Log list should contain one log", 1l, validator.getLogs().size());
        assertEquals("Log message should be " + expectedMessage, expectedMessage, validator.getLogs().get(0).getMessage());
    }

    @Test
    public void test_files() {
        Validator firstValidator = Sie4j.validate(getClass().getResourceAsStream("/sample/Arousells_Visning_AB.SE"));
        assertEquals("First validator should contain 86 logs", 86l, firstValidator.getLogs().size());
        assertEquals("First validator should contain 84 warning", 84l, firstValidator.getWarnings().size());
        assertEquals("First validator should contain 2 info", 2l, firstValidator.getInfo().size());
        Validator secondValidator = Sie4j.validate(getClass().getResourceAsStream("/sample/Transaktioner per Z-rapport.se"));
        assertEquals("Second validator should contain 12 info logs", 12l, secondValidator.getLogs().size());
    }

}
