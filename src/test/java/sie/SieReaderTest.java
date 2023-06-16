package sie;

import sie.log.SieLog;
import java.io.InputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.domain.*;
import sie.exception.SieException;
import sie.validate.*;

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
        String content = SieReader.byteArrayToString(Helper.getSIE(4, 'E'));
        assertTrue(content.contains(expectedProgramLine));
        assertTrue(content.contains(expectedCompanyNameLine));
    }

    @Test
    public void test_StreamReader_encoding_handling() {
        byte[] cp437source = SieReader.streamToByteArray(getStream(""));
        String cp437content = SieReader.byteArrayToString(cp437source);
        byte[] utf8source = SieReader.streamToByteArray(getStream("_UTF_8"));
        String utf8content = SieReader.byteArrayToString(utf8source);
        byte[] is08859source = SieReader.streamToByteArray(getStream("_ISO_8859_15"));
        String iso8859content = SieReader.byteArrayToString(is08859source);
        assertEquals(cp437content, utf8content);
        assertEquals(cp437content, iso8859content);
    }

    @Test
    public void test_checkSumForDocument() {
        String expectedChecksum = "705756706151497349476164793776747166613756673D3D";
        Document cp437doc = Sie4j.fromSie(getStream(""));
        Document utf8doc = Sie4j.fromSie(getStream("_UTF_8"));
        Document iso8859doc = Sie4j.fromSie(getStream("_ISO_8859_15"));
        assertTrue(cp437doc.optChecksum().isPresent());
        assertTrue(utf8doc.optChecksum().isPresent());
        assertTrue(iso8859doc.optChecksum().isPresent());
        assertEquals(expectedChecksum, cp437doc.optChecksum().get());
        assertEquals(cp437doc.optChecksum().get(), utf8doc.optChecksum().get());
        assertEquals(cp437doc.optChecksum().get(), iso8859doc.optChecksum().get());

    }

    @Test
    public void test_BLA_Sie_SI_File() {
        Document doc = Sie4j.fromSie(asByteArray("/sample/CC3.SI"));
        assertTrue(doc.metaData().sieType().equals(Document.Type.I4));
        assertTrue(doc.optAccountingPlan().isPresent());
        AccountingPlan accountingPlan = doc.optAccountingPlan().get();
        Integer expectedNumberOfAccounts = 194;
        assertEquals(expectedNumberOfAccounts, Integer.valueOf(accountingPlan.accounts().size()));
        long expectedNumberOfSruCodes = 202;
        long noOfSruCodes = accountingPlan.accounts().stream().flatMap(acc -> acc.sruCodes().stream()).count();
        assertEquals(expectedNumberOfSruCodes, noOfSruCodes);
    }

    @Test
    public void test_BLA_Sie_SE_File() {
        Document doc = Sie4j.fromSie(asByteArray("/sample/CC2-foretaget.SE"));
        assertTrue(doc.metaData().sieType().equals(Document.Type.E4));
        assertTrue(doc.optAccountingPlan().isPresent());
        AccountingPlan accountingPlan = doc.optAccountingPlan().get();
        Integer expectedNumberOfAccounts = 206;
        assertEquals(expectedNumberOfAccounts, Integer.valueOf(accountingPlan.accounts().size()));
        long expectedNumberOfSruCodes = 213;
        long noOfSruCodes = accountingPlan.accounts().stream().flatMap(acc -> acc.sruCodes().stream()).count();
        assertEquals(expectedNumberOfSruCodes, noOfSruCodes);
        long expectedNumberOfClosingBalances = 7;
        long noOfClosingBalances = accountingPlan.accounts().stream().flatMap(acc -> acc.closingBalances().stream()).count();
        assertEquals(expectedNumberOfClosingBalances, noOfClosingBalances);
        long expectedNumberOfResults = 3;
        long noOfResults = accountingPlan.accounts().stream().flatMap(acc -> acc.results().stream()).count();
        assertEquals(expectedNumberOfResults, noOfResults);
        assertEquals(5l, doc.vouchers().size());
        long expectedNumberOfTransactions = 19;
        long noOfTransactions = doc.vouchers().stream().flatMap(ver -> ver.transactions().stream()).count();
        assertEquals(expectedNumberOfTransactions, noOfTransactions);
    }

    @Test
    public void test_readFaultyAddress() {
        Document doc = Sie4j.fromSie(asByteArray("/sample/BLBLOV_SIE4_UTF_8_WITH_FAULTY_ADDRESS.SI"));
        Optional<Address> optAddr = doc.metaData().getCompany().optAddress();
        assertTrue(optAddr.isPresent());
        Address address = optAddr.get();
        assertFalse(address.isEmpty());
        String expectedContact = "Ada Adamsson";
        String expectedStreet = "Fjärde långatan 127";
        String expectedPostalAddress = "413 05 Göteborg";
        String expectedEmptyPhone = "";
        assertEquals(expectedContact, address.contact());
        assertEquals(expectedStreet, address.streetAddress());
        assertEquals(expectedPostalAddress, address.postalAddress());
        assertEquals(expectedEmptyPhone, address.phone());
    }

    @Test
    public void SIE_file_where_program_version_is_missing_should_be_handled() {
        Document doc = Sie4j.fromSie(asByteArray("/sample/SIE_with_missing_program_version.se"));
        String expectedFirstVoucherText = "Dagsrapport 110000775";
        assertNull(doc.metaData().program().version());
        assertEquals(3l, doc.vouchers().size());
        assertTrue(doc.vouchers().get(0).optText().isPresent());
        assertEquals(expectedFirstVoucherText, doc.vouchers().get(0).optText().get());
    }

    @Test
    public void test_strange_sie_file() {
        Document strangeDoc = Sie4j.fromSie(asByteArray("/sample/Transaktioner per Z-rapport.se"));
        Company company = strangeDoc.metaData().getCompany();
        String expectedCid = "555555-5555";
        assertTrue(company.optCorporateId().isPresent());
        assertEquals(expectedCid, company.optCorporateId().get());
    }

    @Test
    public void test_SIE2_file_with_errors() {
        DataReader reader = SieReader.from(asByteArray("/sample/BLBLOV_SIE2_UTF_8_with_multiple_errors.SE"));
        DocumentValidator validator = reader.validate();
        assertFalse(validator.getLogs().isEmpty());
        assertEquals(4l, validator.getWarnings().size());
    }

    @Test
    public void test_accountingPlan_sie2() {
        DataReader reader = SieReader.from(asByteArray("/sample/BLBLOV_SIE2_UTF_8_with_errors.SE"));
        Document doc = reader.read();
        Document.Type type = doc.metaData().sieType();
        List<SieLog> logs = reader.validate().getLogs();
        long numberOfLogs = 7;
        String logMessage2 = "Kontonummer ska innehålla minst fyra siffror: 119";
        SieLog.Level level2 = SieLog.Level.WARNING;
        String tag3 = "#KONTO";
        String origin4 = AccountingPlan.class.getSimpleName();
        SieLog log1 = logs.get(1);
        assertEquals(numberOfLogs, logs.size());
        assertEquals(logMessage2, log1.getMessage());
        assertEquals(level2, log1.getLevel());
        assertTrue(log1.getTag().isPresent());
        assertEquals(tag3, log1.getTag().get());
        assertTrue(log1.getOrigin().isPresent());
        assertEquals(origin4, log1.getOrigin().get());

        String logMessage6 = "SRU-kod för konto 1110 saknas";
        SieLog.Level level5 = SieLog.Level.INFO;
        String tag2 = "#SRU";
        String origin2 = AccountingPlan.class.getSimpleName();
        SieLog log2 = logs.get(6);
        assertEquals(numberOfLogs, logs.size());
        assertEquals(logMessage6, log2.getMessage());
        assertEquals(level5, log2.getLevel());
        assertTrue(log2.getTag().isPresent());
        assertEquals(tag2, log2.getTag().get());
        assertTrue(log2.getOrigin().isPresent());
        assertEquals(origin2, log2.getOrigin().get());
    }

    @Test
    public void test_accountingPlan_with_missing_account_numbers() {
        DataReader reader = SieReader.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        String expectedMessage = "Kontonummer saknas";
        assertFalse(reader.validate().isValid());
        SieException thrown = assertThrows(SieException.class, () -> reader.read());
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void test_accountingPlan() {
        DataReader reader = SieReader.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_errors.SE"));
        Document doc = reader.read();
        DocumentValidator validator = reader.validate();
        List<SieLog> logs = validator.getLogs();
        long numberOfLogs = 2;
        String logMessage1 = "Konto 11AF saknas i kontolistan";
        String logMessage2 = "Organisationsnummer ska vara av formatet nnnnnn-nnnn. 1655710918";
        SieLog.Level level1 = SieLog.Level.INFO;
        String tag1 = "#ORGNR";
        String origin1 = Document.class.getSimpleName();
        SieLog log1 = logs.get(0);
        SieLog log2 = logs.get(1);
        assertEquals(numberOfLogs, logs.size());
        assertEquals(logMessage1, log1.getMessage());
        assertEquals(logMessage2, log2.getMessage());
        assertEquals(level1, log2.getLevel());
        assertTrue(log2.getTag().isPresent());
        assertEquals(tag1, log2.getTag().get());
        assertTrue(log2.getOrigin().isPresent());
        assertEquals(origin1, log2.getOrigin().get());
    }

    @Test
    public void test_type3_with_vouchers() {
        DataReader reader = SieReader.from(asByteArray("/sample/BLBLOV_SIE3_UTF_8_with_vouchers.SE"));
        DocumentValidator validator = reader.validate();
        String expectedWarningMessage = "Filer av typen E3 får inte innehålla verifikationer";
        assertEquals(6l, validator.getLogs().size());
        assertEquals(2l, validator.getWarnings().size());
        assertEquals(expectedWarningMessage, validator.getWarnings().get(1).getMessage());
    }

    @Test
    public void test_type4E_with_imbalanced_voucher() {
        DataReader reader = SieReader.of(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_imbalanced_voucher.SE"), true);
        DocumentValidator validator = reader.validate();
        String expectedMessage = "Verifikationen A 1 är i obalans. \n"
                + " Differens: 0.10";
        String expectedLine = "#VER \"A\" 1 20170101 \"Representation måltid utan alkohol\" 20170119 \"#6 Linda Henriksson\" ";
        assertEquals(3l, validator.getLogs().size());
        assertEquals(1l, validator.getCriticalErrors().size());
        assertEquals(expectedMessage, validator.getCriticalErrors().get(0).getMessage());
        assertEquals(expectedLine, validator.getCriticalErrors().get(0).getLine().orElse(""));
    }

    @Test
    public void test_compare_original_and_copy() {
        // The copy will have a correct format for the corporate id.
        DataReader original = SieReader.from(asByteArray("/sample/BLBLOV_SIE1.SE"));
        DataReader copy = SieReader.from(asByteArray("/sample/BLBLOV_SIE1_copy.SE"));
        String originalLog = "SieLog{origin=Document, level=INFO, tag=#ORGNR, message=Organisationsnummer ska vara av formatet nnnnnn-nnnn. 1655710918}";
        assertEquals(1, original.validate().getLogs().size());
        assertEquals(originalLog, original.validate().getLogs().get(0).toString());
        assertTrue(original.read().metaData().program()
                .equals(copy.read().metaData().program()));
    }

    @Test
    public void test_that_input_null_throws_SieException() {
        String expectedMessage = "Källan får inte vara null";
        InputStream input = null;
        SieException ex = assertThrows(SieException.class, () -> SieReader.createReader(input, true));
        assertEquals(expectedMessage, ex.getMessage());
    }

    private InputStream getStream(String string) {
        String path = "/sample/BLBLOV_SIE4";
        String suffix = ".SI";
        return getClass().getResourceAsStream(path + string + suffix);
    }

    private byte[] asByteArray(String path) {
        return SieReader.streamToByteArray(getClass().getResourceAsStream(path));
    }
}
