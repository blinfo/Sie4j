package sie;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.AccountingPlan;
import sie.domain.Address;
import sie.domain.Company;
import sie.domain.Document;
import sie.validate.DocumentValidator;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 *
 */
public class SieReaderTest {

    @Test
    public void test_StreamReader_read() {
        String expectedProgramLine = "#PROGRAM \"BL Administration\" 2018.2.101";
        String expectedCompanyNameLine = "#FNAMN \"Övningsföretaget AB\"";
        String content = SieReader.streamToString(Helper.getSIE(4, 'E'));
        assertTrue("Should contain " + expectedProgramLine, content.contains(expectedProgramLine));
        assertTrue("Should contain " + expectedCompanyNameLine, content.contains(expectedCompanyNameLine));
    }

    @Test
    public void test_StreamReader_encoding_handling() {
        String cp437content = SieReader.streamToString(getStream(""));
        String utf8content = SieReader.streamToString(getStream("_UTF_8"));
        String iso8859content = SieReader.streamToString(getStream("_ISO_8859_15"));
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

    @Test
    public void test_readFaultyAddress() {
        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_WITH_FAULTY_ADDRESS.SI"));
        Optional<Address> optAddr = doc.getMetaData().getCompany().getAddress();
        assertTrue("Document should have an address", optAddr.isPresent());
        Address address = optAddr.get();
        assertFalse("Address should not be empty", address.isEmpty());
        String expectedContact = "Ada Adamsson";
        String expectedStreet = "Fjärde långatan 127";
        String expectedPostalAddress = "413 05 Göteborg";
        assertEquals("Contact should be ", expectedContact, address.getContact());
        assertEquals("Street should be ", expectedStreet, address.getStreetAddress());
        assertEquals("Postal address should be ", expectedPostalAddress, address.getPostalAddress());
        assertNull("Phone should be null", address.getPhone());
    }

    @Test
    public void SIE_file_where_program_version_is_missing_should_be_handled() {
        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/SIE_with_missing_program_version.se"));
        String expectedFirstVoucherText = "Dagsrapport 110000775";
        assertNull("Version should be null", doc.getMetaData().getProgram().getVersion());
        assertEquals("Document should contain 3 vouchers", 3l, doc.getVouchers().size());
        assertTrue("First voucher should have a text", doc.getVouchers().get(0).getText().isPresent());
        assertEquals("First voucher text should be " + expectedFirstVoucherText, expectedFirstVoucherText, doc.getVouchers().get(0).getText().get());
    }

    @Test
    public void test_strange_sie_file() {
        Document strangeDoc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/Transaktioner per Z-rapport.se"));
        Company company = strangeDoc.getMetaData().getCompany();
        String expectedCid = "555555-5555";
        assertTrue("CID should be present", company.getCorporateID().isPresent());
        assertEquals("CID should be " + expectedCid, expectedCid, company.getCorporateID().get());
    }

    private InputStream getStream(String string) {
        String path = "/sample/BLBLOV_SIE4";
        String suffix = ".SI";
        return getClass().getResourceAsStream(path + string + suffix);
    }

    @Test
    public void test_SIE2_file_with_errors() {
        SieReader reader = SieReader.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE2_UTF_8_with_errors.SE"));
        DocumentValidator validator = reader.validate();
        assertFalse("Logs should not be empty", validator.getLogs().isEmpty());
        assertEquals("Should contain 5 warnings", 5l, validator.getWarnings().size());
    }

    @Test
    public void test_accountingPlan_sie2() {
        SieReader reader = SieReader.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE2_UTF_8_with_errors.SE"));
        Document doc = reader.read();
        Document.Type type = doc.getMetaData().getSieType();
        List<SieLog> logs = reader.validate().getLogs();
        long numberOfLogs = 7;
        String logMessage4 = "Kontot har inte ett numeriskt värde: 11AF";
        SieLog.Level level4 = SieLog.Level.WARNING;
        String tag4 = "#KONTO";
        String origin4 = AccountingPlan.class.getSimpleName();
        SieLog log1 = logs.get(4);
        assertEquals("Should contain " + numberOfLogs + " logs", numberOfLogs, logs.size());
        assertEquals("Log message should be " + logMessage4, logMessage4, log1.getMessage());
        assertEquals("Log level should be " + level4, level4, log1.getLevel());
        assertTrue("Log should have a tag", log1.getTag().isPresent());
        assertEquals("Log tag should be " + tag4, tag4, log1.getTag().get());
        assertTrue("Log should have an origin", log1.getOrigin().isPresent());
        assertEquals("Log origin should be " + origin4, origin4, log1.getOrigin().get());

        String logMessage2 = "SRU-kod för konto 1110 saknas";
        SieLog.Level level2 = SieLog.Level.INFO;
        String tag2 = "#SRU";
        String origin2 = AccountingPlan.class.getSimpleName();
        SieLog log2 = logs.get(1);
        assertEquals("Should contain " + numberOfLogs + " logs", numberOfLogs, logs.size());
        assertEquals("Log message should be " + logMessage2, logMessage2, log2.getMessage());
        assertEquals("Log level should be " + level2, level2, log2.getLevel());
        assertTrue("Log should have a tag", log2.getTag().isPresent());
        assertEquals("Log tag should be " + tag2, tag2, log2.getTag().get());
        assertTrue("Log should have an origin", log2.getOrigin().isPresent());
        assertEquals("Log origin should be " + origin2, origin2, log2.getOrigin().get());
    }

    @Test
    public void test_accountingPlan_with_missing_account_numbers() {
        SieReader reader = SieReader.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        String expectedMessage = "Kontonummer får inte vara null eller tom sträng";
        assertFalse("Should not be valid", reader.validate().isValid());
        SieException thrown = assertThrows("", SieException.class, () -> reader.read());
        assertEquals("Message should be " + expectedMessage, expectedMessage, thrown.getMessage());
    }

    @Test
    public void test_accountingPlan() {
        SieReader reader = SieReader.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_errors.SE"));
        Document doc = reader.read();
        DocumentValidator validator = reader.validate();
        List<SieLog> logs = validator.getLogs();
        long numberOfLogs = 27;
        String logMessage1 = "Kontot har inte ett numeriskt värde: 11AF";
        SieLog.Level level1 = SieLog.Level.WARNING;
        String tag1 = "#KONTO";
        String origin1 = AccountingPlan.class.getSimpleName();
        SieLog log1 = logs.get(26);
        assertEquals("Should contain " + numberOfLogs + " logs", numberOfLogs, logs.size());
        assertEquals("Log message should be " + logMessage1, logMessage1, log1.getMessage());
        assertEquals("Log level should be " + level1, level1, log1.getLevel());
        assertTrue("Log should have a tag", log1.getTag().isPresent());
        assertEquals("Log tag should be " + tag1, tag1, log1.getTag().get());
        assertTrue("Log should have an origin", log1.getOrigin().isPresent());
        assertEquals("Log origin should be " + origin1, origin1, log1.getOrigin().get());
    }

    @Test
    public void test_type3_with_vouchers() {
        SieReader reader = SieReader.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE3_UTF_8_with_vouchers.SE"));
        DocumentValidator validator = reader.validate();
        String expectedWarningMessage = "Filer av typen E3 får inte innehålla verifikationer";
        assertEquals("Log list should contain one log", 3l, validator.getLogs().size());
        assertEquals("Log list should contain two warnings", 2l, validator.getWarnings().size());
        assertEquals("Second warning message should be " + expectedWarningMessage, expectedWarningMessage, validator.getWarnings().get(1).getMessage());
    }

    @Test
    public void test_type4E_with_imbalanced_voucher() {
        SieReader reader = SieReader.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_imbalanced_voucher.SE"));
        DocumentValidator validator = reader.validate();
        String expectedMessage = "Verifikationen är i obalans. Serie: A. Nummer: 1. Differens: 0.10";
        assertEquals("Log list should contain one log", 27l, validator.getLogs().size());
        assertEquals("Log list should contain one critical error", 1l, validator.getCriticalErrors().size());
        assertEquals("Log message should be " + expectedMessage, expectedMessage, validator.getCriticalErrors().get(0).getMessage());
    }
}
