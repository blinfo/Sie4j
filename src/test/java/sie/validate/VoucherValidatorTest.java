package sie.validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void test_type3_with_vouchers() {
        Document document = getDocument("BLBLOV_SIE3_UTF_8_with_vouchers.SE");
        Document.Type type = document.getMetaData().getSieType();
        Voucher voucher = document.getVouchers().get(0);
        VoucherValidator validator = VoucherValidator.of(voucher, type);
        assertEquals("Error list should contain one error", 1l, validator.getErrors().size());
        assertEquals("Error list should contain one fatal error", 1l, validator.getFatalErrors().size());
    }

    @Test
    public void test_type4E_with_imbalanced_voucher() {
        Document document = getDocument("BLBLOV_SIE4_UTF_8_with_imbalanced_voucher.SE");
        Document.Type type = document.getMetaData().getSieType();
        Voucher voucher = document.getVouchers().get(0);
        VoucherValidator validator = VoucherValidator.of(voucher, type);
        String expectedMessage = "Verifikationen är i obalans. Serie: A. Nummer: 1. Differens: 0.10";
        assertEquals("Error list should contain one error", 1l, validator.getErrors().size());
        assertEquals("Error list should contain one fatal error", 1l, validator.getFatalErrors().size());
        assertEquals("Error message should be " + expectedMessage, expectedMessage, validator.getErrors().get(0).getMessage());
    }

    @Test
    public void test_type4E_with_empty_voucher() {
        Document document = getDocument("BLBLOV_SIE4_UTF_8_with_empty_voucher.SE");
        Document.Type type = document.getMetaData().getSieType();
        Voucher voucher = document.getVouchers().get(1);
        VoucherValidator validator = VoucherValidator.of(voucher, type);
        String expectedMessage = "Verifikationen innehåller inga transaktionsrader. Serie: A. Nummer: 2.";
        assertEquals("Error list should contain one error", 1l, validator.getErrors().size());
        assertEquals("Error message should be " + expectedMessage, expectedMessage, validator.getErrors().get(0).getMessage());
    }

    @Test
    public void test_files() {
        Validator firstValidator = Sie4j.validate(getClass().getResourceAsStream("/sample/Arousells_Visning_AB.SE"));
        assertEquals("First validator should contain 42 errors", 42l, firstValidator.getErrors().size());
        assertEquals("First validator should contain 1 warning", 1l, firstValidator.getWarnings().size());
        assertEquals("First validator should contain 1 info", 1l, firstValidator.getInfo().size());
        assertEquals("First validator should contain 40 fatal", 40l, firstValidator.getFatalErrors().size());
        Validator secondValidator = Sie4j.validate(getClass().getResourceAsStream("/sample/Transaktioner per Z-rapport.se"));
        assertTrue("Second validator should contain no errors", secondValidator.getErrors().isEmpty());
    }

}
