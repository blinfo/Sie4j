package sie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.Account;
import sie.domain.AccountingPlan;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.FinancialYear;
import sie.domain.Generated;
import sie.domain.MetaData;
import sie.domain.Program;
import sie.domain.Transaction;
import sie.domain.Voucher;
import sie.sample.SampleDocumentGenerator;
import sie.sample.Version;

/**
 *
 * @author Håkan Lidén
 */
public class SieWriterTest {

    @Test
    public void test_SieWriter_fromDocument() {
        Document sampleDoc = SampleDocumentGenerator.generate();
        String expectedVersion = Version.current().toString();
        String sieString = SieWriter.write(sampleDoc);
        assertTrue("String should contain #FORMAT PC8", sieString.contains("PC8"));
        assertTrue("String should contain #PROGRAM \"Sie4j\" \"" + expectedVersion + "\"", sieString.contains("#PROGRAM \"Sie4j\" \"" + expectedVersion + "\""));
        assertTrue("String should contain #FNAMN \"", sieString.contains("#FNAMN \""));
        assertTrue("String should contain #RAR 0", sieString.contains("#RAR 0"));
        assertTrue("String should contain #TAXAR ", sieString.contains("#TAXAR "));
        assertTrue("String should contain #KPTYP ", sieString.contains("#KPTYP "));
        assertTrue("String should contain #KONTO ", sieString.contains("#KONTO "));
        assertTrue("String should contain #VER ", sieString.contains("#VER "));
        assertTrue("String should contain #TRANS ", sieString.contains("#TRANS "));
    }

    @Test
    public void test_SieWriter_with_created_document() {
        Document doc = Document.builder()
                .metaData(createMetaData())
                .accountingPlan(createAccountingPlan())
                .vouchers(createVouchers())
                .apply();
        String result = Sie4j.fromDocument(doc);
        String expectedResult = getResult();
        assertEquals("Result should be " + expectedResult, expectedResult, result);
    }

    private MetaData createMetaData() {
        return MetaData.builder()
                .company(Company.builder("Test av Writer").corporateID("123432-9878").apply())
                .program(Program.of("SieWriterTest", "0.0.1"))
                .generated(Generated.from(LocalDate.parse("2021-08-18")))
                .read(Boolean.FALSE)
                .sieType(Document.Type.E4)
                .financialYears(Arrays.asList(FinancialYear.of(0, LocalDate.parse("2021-01-01"), LocalDate.parse("2021-12-31"))))
                .apply();
    }

    private AccountingPlan createAccountingPlan() {
        return AccountingPlan.builder().accounts(Arrays.asList(
                createAccount("1000", "Apelsin"),
                createAccount("1001", "Banan"),
                createAccount("1002", "Citron")))
                .apply();
    }

    private Account createAccount(String number, String label) {
        return Account.builder(number).label(label).apply();
    }

    private List<Voucher> createVouchers() {
        return Arrays.asList(createVoucher(1, ""), createVoucher(2, null), createVoucher(1, "A"));
    }

    private Voucher createVoucher(Integer number, String series) {
        return Voucher.builder().date(LocalDate.parse("2021-02-0" + number)).series(series).number(number)
                .addTransaction(Transaction.builder().accountNumber("1000").amount(BigDecimal.ONE).apply())
                .addTransaction(Transaction.builder().accountNumber("1001").amount(BigDecimal.ONE.negate()).apply())
                .apply();
    }
    
    private String getResult() {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/SieWriterTest-result.si"), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
