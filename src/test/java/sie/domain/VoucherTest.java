package sie.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.Helper;
import sie.Sie4j;
import static sie.domain.Entity.ROUNDING_MODE;
import static sie.domain.Entity.SCALE;

/**
 *
 * @author Håkan Lidén
 *
 */
public class VoucherTest extends Helper {

    @Test
    public void test_Vouchers_are_balanced() {
        getDocument(4, 'I').getVouchers().stream()
                .forEach(v -> {
                    assertTrue("Voucher is balanced", v.isBalanced());
                    assertTrue("Voucher has transactions", v.getTransactions().size() > 0);
                });

        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_IMBALANCED.SI"));
        assertFalse("Document contains unbalanced voucchers", doc.isBalanced());
        Integer expectedNumberOfVouchers = 2;
        BigDecimal expectedFirstDiff = new BigDecimal(0.55).setScale(SCALE, ROUNDING_MODE);
        BigDecimal expectedSecondDiff = new BigDecimal(-0.14).setScale(SCALE, ROUNDING_MODE);
        List<Voucher> imbalancedVouchers = doc.getImbalancedVouchers();
        assertEquals("Document should contain " + expectedNumberOfVouchers + " imbalanced vouchers",
                expectedNumberOfVouchers, Integer.valueOf(imbalancedVouchers.size()));
        assertEquals("First diff should be " + expectedFirstDiff, expectedFirstDiff, imbalancedVouchers.get(0).getDiff());
        assertEquals("Second diff should be " + expectedSecondDiff, expectedSecondDiff, imbalancedVouchers.get(1).getDiff());
    }

    @Test
    public void test_Voucher() {
        List<Voucher> vouchers = getDocument(4, 'E').getVouchers();
        Voucher firstVoucher = vouchers.get(0);
        String firstText = "Representation måltid utan alkohol";
        String signature = "#6 Linda Henriksson";
        LocalDate firstDate = LocalDate.parse("2017-01-01");
        LocalDate firstRegDate = LocalDate.parse("2017-01-19");
        assertTrue("First voucher should have a series", firstVoucher.getSeries().isPresent());
        assertEquals("First voucher series should be A", "A", firstVoucher.getSeries().get());
        assertTrue("First voucher should have a number", firstVoucher.getNumber().isPresent());
        assertEquals("First voucher number should be 1", Integer.valueOf(1), firstVoucher.getNumber().get());
        assertTrue("First voucher should be balanced", firstVoucher.isBalanced());
        assertEquals("First voucher should have 3 transactions", Integer.valueOf(3), Integer.valueOf(firstVoucher.getTransactions().size()));
        assertTrue("First voucher should have text", firstVoucher.getText().isPresent());
        assertEquals("First voucher text should be " + firstText, firstText, firstVoucher.getText().get());
        assertEquals("First voucher date should be " + firstDate, firstDate, firstVoucher.getDate());
        assertTrue("First voucher should have registration date", firstVoucher.getRegistrationDate().isPresent());
        assertEquals("First voucher registration date should be " + firstRegDate, firstRegDate, firstVoucher.getRegistrationDate().get());
        assertTrue("First voucher should have signature", firstVoucher.getSignature().isPresent());
        assertEquals("First voucher signature should be " + signature, signature, firstVoucher.getSignature().get());

        Voucher lastVoucher = vouchers.get(vouchers.size() - 1);
        String lastText = "Löner";
        LocalDate lastDate = LocalDate.parse("2017-01-25");
        LocalDate lastRegDate = LocalDate.parse("2017-03-22");
        assertTrue("Last voucher should have a series", lastVoucher.getSeries().isPresent());
        assertEquals("Last voucher series should be N", "N", lastVoucher.getSeries().get());
        assertTrue("Last voucher should have a number", lastVoucher.getNumber().isPresent());
        assertEquals("Last voucher number should be 1", Integer.valueOf(1), lastVoucher.getNumber().get());
        assertTrue("Last voucher should be balanced", lastVoucher.isBalanced());
        assertEquals("Last voucher should have 5 transactions", Integer.valueOf(5), Integer.valueOf(lastVoucher.getTransactions().size()));
        assertTrue("Last voucher should have text", lastVoucher.getText().isPresent());
        assertEquals("Last voucher text should be " + lastText, lastText, lastVoucher.getText().get());
        assertEquals("Last voucher date should be " + lastDate, lastDate, lastVoucher.getDate());
        assertTrue("Last voucher should have registration date", lastVoucher.getRegistrationDate().isPresent());
        assertEquals("Last voucher registration date should be " + lastRegDate, lastRegDate, lastVoucher.getRegistrationDate().get());
        assertTrue("Last voucher should have signature", lastVoucher.getSignature().isPresent());
        assertEquals("Last voucher signature should be " + signature, signature, lastVoucher.getSignature().get());
    }

    @Test
    public void test_Transaction() {
        List<Voucher> vouchers = getDocument(4, 'E').getVouchers();
        List<Transaction> transactions = vouchers.get(vouchers.size() - 1).getTransactions();
        Transaction first = transactions.get(0);
        BigDecimal firstAmount = new BigDecimal(-24268).setScale(SCALE, ROUNDING_MODE);
        LocalDate date = LocalDate.parse("2017-01-25");
        assertEquals("First transaction should have amount " + firstAmount, firstAmount, first.getAmount());
        assertEquals("First transaction should have account 1930", "1930", first.getAccountNumber());
        assertTrue("First transaction should have a date", first.getDate().isPresent());
        assertEquals("First transaction date should be " + date, date, first.getDate().get());

        Transaction last = transactions.get(transactions.size() - 1);
        BigDecimal lastAmount = new BigDecimal(-10350.38).setScale(SCALE, ROUNDING_MODE);
        assertEquals("Last transaction should have amount " + lastAmount, lastAmount, last.getAmount());
        assertEquals("Last transaction should have account 2730 ", "2730", last.getAccountNumber());
        assertTrue("Last transaction should have a date", last.getDate().isPresent());
        assertEquals("Last transaction date should be " + date, date, last.getDate().get());
    }
}