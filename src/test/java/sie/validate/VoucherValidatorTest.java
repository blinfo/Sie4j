package sie.validate;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.Sie4j;
import sie.domain.Document;
import sie.domain.Voucher;
import sie.dto.ValidationResultDTO;

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

    @Test
    public void test_files() {
        ValidationResultDTO result1 = Sie4j.validate(getClass().getResourceAsStream("/sample/Arousells_Visning_AB.SE"));
        long expectedNumberOfLogs = 67l;
        long expectedNumberOfWarnings = 65l;
        assertEquals("First validator should contain " + expectedNumberOfLogs + "  logs", expectedNumberOfLogs, result1.getLogs().size());
        assertEquals("First validator should contain " + expectedNumberOfWarnings + " warning", expectedNumberOfWarnings, result1.getWarnings().size());
        assertEquals("First validator should contain 2 info", 2l, result1.getInfos().size());
        ValidationResultDTO result2 = Sie4j.validate(getClass().getResourceAsStream("/sample/Transaktioner per Z-rapport.se"));
        assertEquals("Second validator should contain 3 info logs", 3l, result2.getLogs().size());
    }

}
