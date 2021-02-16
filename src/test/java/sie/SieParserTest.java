package sie;

import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.AccountingPlan;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 *
 */
public class SieParserTest {

    @Test
    public void test_StreamReader_read() {
        String expectedProgramLine = "#PROGRAM \"BL Administration\" 2018.2.101";
        String expectedCompanyNameLine = "#FNAMN \"Övningsföretaget AB\"";
        String content = SieParser.read(Helper.getSIE(4, 'E'));
        assertTrue("Should contain " + expectedProgramLine, content.contains(expectedProgramLine));
        assertTrue("Should contain " + expectedCompanyNameLine, content.contains(expectedCompanyNameLine));
    }

    @Test
    public void test_StreamReader_encoding_handling() {
        String cp437content = SieParser.read(getStream(""));
        String utf8content = SieParser.read(getStream("_UTF_8"));
        String iso8859content = SieParser.read(getStream("_ISO_8859_15"));
        assertEquals("Content should be same", cp437content, utf8content);
        assertEquals("Content should be same", cp437content, iso8859content);
    }

    @Test
    public void test_checkSumForDocument() {
        String expectedChecksum = "A5656969022C20669DCBBBEDA9F6BB56";
        Document cp437doc = Sie4j.toDocument(getStream(""));
        Document utf8doc = Sie4j.toDocument(getStream("_UTF_8"));
        Document iso8859doc = Sie4j.toDocument(getStream("_ISO_8859_15"));
        assertTrue("Document checksum should exist", cp437doc.getChecksum().isPresent());
        assertTrue("Document checksum should exist", utf8doc.getChecksum().isPresent());
        assertTrue("Document checksum should exist", iso8859doc.getChecksum().isPresent());
        assertEquals("Checksum should equal " + expectedChecksum, expectedChecksum, cp437doc.getChecksum().get());
        assertEquals("Checksum should be same", cp437doc.getChecksum().get(), utf8doc.getChecksum().get());
        assertEquals("Checksum should be same", cp437doc.getChecksum().get(), iso8859doc.getChecksum().get());

    }

    @Test
    public void test_BLA_Sie_SI_File() {
        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/CC3.SI"));
        assertTrue("Document should be of type I4", doc.getMetaData().getSieType().equals(Document.Type.I4));
        assertTrue("AccountingPlan should exist", doc.getAccountingPlan().isPresent());
        AccountingPlan accountingPlan = doc.getAccountingPlan().get();
        Integer expectedNumberOfAccounts = 194;
        assertEquals("AccountingPlan should have 194 accounts", expectedNumberOfAccounts, Integer.valueOf(accountingPlan.getAccounts().size()));
        long expectedNumberOfSruCodes = 202;
        long noOfSruCodes = accountingPlan.getAccounts().stream().flatMap(acc -> acc.getSruCodes().stream()).count();
        assertEquals("AccountingPlan should have 202 sru codes", expectedNumberOfSruCodes, noOfSruCodes);
    }

    @Test
    public void test_BLA_Sie_SE_File() {
        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/CC2-foretaget.SE"));
        assertTrue("Document should be of type E4", doc.getMetaData().getSieType().equals(Document.Type.E4));
        assertTrue("AccountingPlan should exist", doc.getAccountingPlan().isPresent());
        AccountingPlan accountingPlan = doc.getAccountingPlan().get();
        Integer expectedNumberOfAccounts = 206;
        assertEquals("AccountingPlan should have 206 accounts", expectedNumberOfAccounts, Integer.valueOf(accountingPlan.getAccounts().size()));
        long expectedNumberOfSruCodes = 213;
        long noOfSruCodes = accountingPlan.getAccounts().stream().flatMap(acc -> acc.getSruCodes().stream()).count();
        assertEquals("AccountingPlan should have 213 sru codes", expectedNumberOfSruCodes, noOfSruCodes);
        long expectedNumberOfClosingBalances = 7;
        long noOfClosingBalances = accountingPlan.getAccounts().stream().flatMap(acc -> acc.getClosingBalances().stream()).count();
        assertEquals("AccountingPlan should have 7 closing balances", expectedNumberOfClosingBalances, noOfClosingBalances);
        long expectedNumberOfResults = 3;
        long noOfResults = accountingPlan.getAccounts().stream().flatMap(acc -> acc.getResults().stream()).count();
        assertEquals("AccountingPlan should have 3 results", expectedNumberOfResults, noOfResults);
        assertEquals("Document should have 5 vouchers", 5l, doc.getVouchers().size());
        long expectedNumberOfTransactions = 19;
        long noOfTransactions = doc.getVouchers().stream().flatMap(ver -> ver.getTransactions().stream()).count();
        assertEquals("Document should have ", expectedNumberOfTransactions, noOfTransactions);
        
    }

    private InputStream getStream(String string) {
        String path = "/sample/BLBLOV_SIE4";
        String suffix = ".SI";
        return getClass().getResourceAsStream(path + string + suffix);
    }
}
