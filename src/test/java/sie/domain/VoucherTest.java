package sie.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.*;
import static sie.domain.Entity.*;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 *
 */
public class VoucherTest extends Helper {

    @Test
    public void test_Vouchers_are_balanced() {
        getDocument(4, 'I').vouchers().stream()
                .forEach(v -> {
                    assertTrue(v.balanced());
                    assertTrue(v.transactions().size() > 0);
                });
        String expectedMessage = "Verifikationen är i obalans. \n"
                + " Differens: -0.14\n"
                + " #VER \"K\" \"\" 20180502 \"Försäljning 12% (DF:158)\"  20180503\n"
                + " - Verifikationen är i obalans. \n"
                + " Differens: 0.55\n"
                + " #VER \"K\" \"\" 20180502 \"Försäljning 25% (DF:157)\"  20180503";
        SieException ex = assertThrows(SieException.class, () -> Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_IMBALANCED.SI")));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void test_Voucher_isBalanced() {
        Transaction t1 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("10.005")).apply();
        Transaction t2 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("-10.00")).apply();
        Voucher v1 = Voucher.builder().addTransaction(t1).addTransaction(t2).date(LocalDate.now()).number(21).series("A").apply();
        assertFalse(v1.balanced());
        Transaction t3 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("10.004")).apply();
        Transaction t4 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("-10.00")).apply();
        Voucher v2 = Voucher.builder().addTransaction(t3).addTransaction(t4).date(LocalDate.now()).number(21).series("A").apply();
        assertTrue(v2.balanced());
    }

    @Test
    public void test_Voucher_getDiff() {
        Transaction t1 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("10.005")).apply();
        Transaction t2 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("-10.00")).apply();
        Voucher v1 = Voucher.builder().addTransaction(t1).addTransaction(t2).date(LocalDate.now()).number(21).series("A").apply();
        assertEquals(new BigDecimal("0.01"), v1.diff());
        Transaction t3 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("10.004")).apply();
        Transaction t4 = Transaction.builder().accountNumber("1910").amount(new BigDecimal("-10.00")).apply();
        Voucher v2 = Voucher.builder().addTransaction(t3).addTransaction(t4).date(LocalDate.now()).number(21).series("A").apply();
        assertEquals(new BigDecimal("0.00"), v2.diff());
    }

    @Test
    public void test_Voucher() {
        List<Voucher> vouchers = getDocument(4, 'E').vouchers();
        Voucher firstVoucher = vouchers.get(0);
        String firstText = "Representation måltid utan alkohol";
        String signature = "#6 Linda Henriksson";
        LocalDate firstDate = LocalDate.parse("2017-01-01");
        LocalDate firstRegDate = LocalDate.parse("2017-01-19");
        assertTrue(firstVoucher.optSeries().isPresent());
        assertEquals("A", firstVoucher.optSeries().get());
        assertTrue(firstVoucher.optNumber().isPresent());
        assertEquals(Integer.valueOf(1), firstVoucher.optNumber().get());
        assertTrue(firstVoucher.balanced());
        assertEquals(Integer.valueOf(3), Integer.valueOf(firstVoucher.transactions().size()));
        assertTrue(firstVoucher.optText().isPresent());
        assertEquals(firstText, firstVoucher.optText().get());
        assertEquals(firstDate, firstVoucher.date());
        assertTrue(firstVoucher.optRegistrationDate().isPresent());
        assertEquals(firstRegDate, firstVoucher.optRegistrationDate().get());
        assertTrue(firstVoucher.optSignature().isPresent());
        assertEquals(signature, firstVoucher.optSignature().get());

        Voucher lastVoucher = vouchers.get(vouchers.size() - 1);
        String lastText = "Löner";
        LocalDate lastDate = LocalDate.parse("2017-01-25");
        LocalDate lastRegDate = LocalDate.parse("2017-03-22");
        assertTrue(lastVoucher.optSeries().isPresent());
        assertEquals("N", lastVoucher.optSeries().get());
        assertTrue(lastVoucher.optNumber().isPresent());
        assertEquals(Integer.valueOf(1), lastVoucher.optNumber().get());
        assertTrue(lastVoucher.balanced());
        assertEquals(Integer.valueOf(5), Integer.valueOf(lastVoucher.transactions().size()));
        assertTrue(lastVoucher.optText().isPresent());
        assertEquals(lastText, lastVoucher.optText().get());
        assertEquals(lastDate, lastVoucher.date());
        assertTrue(lastVoucher.optRegistrationDate().isPresent());
        assertEquals(lastRegDate, lastVoucher.optRegistrationDate().get());
        assertTrue(lastVoucher.optSignature().isPresent());
        assertEquals(signature, lastVoucher.optSignature().get());
    }

    @Test
    public void test_Transaction() {
        List<Voucher> vouchers = getDocument(4, 'E').vouchers();
        List<Transaction> transactions = vouchers.get(vouchers.size() - 1).transactions();
        Transaction first = transactions.get(0);
        BigDecimal firstAmount = new BigDecimal(-24268).setScale(SCALE, ROUNDING_MODE);
        LocalDate date = LocalDate.parse("2017-01-25");
        assertEquals(firstAmount, first.amount());
        assertEquals("1930", first.accountNumber());
        assertTrue(first.optDate().isPresent());
        assertEquals(date, first.optDate().get());

        Transaction last = transactions.get(transactions.size() - 1);
        BigDecimal lastAmount = new BigDecimal(-10350.38).setScale(SCALE, ROUNDING_MODE);
        assertEquals(lastAmount, last.amount());
        assertEquals("2730", last.accountNumber());
        assertTrue(last.optDate().isPresent());
        assertEquals(date, last.optDate().get());
    }

    @Test
    public void test_VoucherText_with_inline_quotes() {
        String expectedResult = "Försäljning 25% \"DF:157\"";
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/Quotes_test.si"));
        assertEquals(expectedResult, doc.vouchers().get(0).optText().orElse(""));
    }

    @Test
    public void test_Voucher_from_strange_sie_file() {
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/SIE_with_missing_program_version.se"));
        List<Voucher> vouchers = doc.vouchers();
        assertEquals(8l, vouchers.get(0).transactions().size());
        assertEquals(7l, vouchers.get(1).transactions().size());
        assertEquals(8l, vouchers.get(2).transactions().size());
    }
}
