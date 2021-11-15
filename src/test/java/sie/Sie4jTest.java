package sie;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.Document;
import sie.domain.Voucher;
import sie.dto.SieLogDTO;
import sie.dto.ValidationResultDTO;
import sie.exception.SieException;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 */
public class Sie4jTest {

    @Test
    public void test_file_with_missing_accounts() {
        ValidationResultDTO validate = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        List<SieLogDTO> logs = validate.getCriticals();
        long numberOfCriticals = 1;
        String level = SieLog.Level.CRITICAL.name();
        String message = "Kontonummer saknas";
        assertEquals("Should contain" + numberOfCriticals + " log", numberOfCriticals, logs.size());
        SieLogDTO log = logs.get(0);
        String origin = Sie4j.class.getSimpleName();
        assertEquals("Level should be " + level, level, log.getLevel());
        assertEquals("Message should be " + message, message, log.getMessage());
        assertEquals("Origin should be " + origin, origin, log.getOrigin());
    }

    @Test
    public void test_file_with_missing_account_balance() {
        List<SieLogDTO> logs = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_balance.SE")).getLogs();
        long numberOfLogs = 1;
        String level = SieLog.Level.CRITICAL.name();
        String message = "Strängen '' för balans, konto 1119, kan inte hanteras som belopp";
        assertEquals("Should contain" + numberOfLogs + " log", numberOfLogs, logs.size());
        SieLogDTO log = logs.get(0);
        String origin = Sie4j.class.getSimpleName();
        assertEquals("Level should be " + level, level, log.getLevel());
        assertEquals("Message should be " + message, message, log.getMessage());
        assertEquals("Origin should be " + origin, origin, log.getOrigin());
    }

    @Test
    public void test_null_input() {
        String message = "Källan får inte vara null";
        InputStream input = null;
        SieException ex = assertThrows("", SieException.class, () -> SieReader.streamToByteArray(input));
        assertEquals("Message should be " + message, message, ex.getMessage());
    }

    @Test
    public void test_stream_throws_IOException() {
        String message = "Kunde inte läsa källan";
        ValidationResultDTO validator = Sie4j.validate("THROW".getBytes());
        assertEquals("Validator should contain 1 log", 1l, validator.getLogs().size());
        assertEquals("Validator should contain 1 critical error", 1l, validator.getCriticals().size());
        SieLogDTO critical = validator.getCriticals().get(0);
        assertEquals("Message should be " + message, message, critical.getMessage());
        assertTrue("Log contains no tag", critical.getTag() == null);
    }

    @Test
    public void test_write_sie_file() {
        Document document = Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE1.SE"));
        File target = new File(System.getProperty("user.home") + "/sie-test/BLBLOV_SIE1_copy.SE");
        assertFalse("File should not exist", target.exists());
        target.getParentFile().mkdirs();
        Sie4j.fromDocument(document, target);
        assertTrue("File should exist", target.exists());
        target.delete();
        assertFalse("File should not exist", target.exists());
    }

    @Test
    public void test_file_with_missing_voucher_series_marker() {
        // If no series is given, an empty string should be used in the SIE file:
        // #VER "" 1 20170101 " " 20170119 " " 
        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_voucher_series.SE"));
        Voucher firstVoucher = doc.getVouchers().get(0);
        assertEquals("First vouchers number should be 1", 1l, firstVoucher.getNumber().get().longValue());
        assertFalse("First vouchers serie should be empty", firstVoucher.getSeries().isPresent());
        assertFalse("First vouchers text should be empty", firstVoucher.getText().isPresent());
        assertFalse("First vouchers signature should be empty", firstVoucher.getSignature().isPresent());
    }

    @Test
    public void test_SIE2_with_non_numeric_account_number_should_throw_exception() {
        SieException ex = assertThrows("", SieException.class, () -> Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE2_UTF_8_with_non_numeric_account_number.SE")));
        String expectedMessage = "Kontot har inte ett numeriskt värde: 11AF";
        assertEquals("Should have message: " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_that_too_long_account_number_throws_exception() {
        SieException ex = assertThrows("", SieException.class, () -> Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_8_digit_account_number.SE")));
        String expectedMessage = "Kontot är längre än sex siffror: 11100111";
        assertEquals("Should have message: " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_validate_file_with_missing_company_name() {
        ValidationResultDTO validator = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_company_name.SI"));
        String expectedMessage = "Företagsnamn saknas";
        assertEquals("Should contain 1 warning", 1l, validator.getWarnings().size());
        assertEquals("Should contain message " + expectedMessage, expectedMessage, validator.getWarnings().get(0).getMessage());
    }

    @Test
    public void test_file_with_missing_company_name() {
        Document doc = Sie4j.toDocument(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_company_name.SI"));
        assertTrue("Company name should be empty", doc.getMetaData().getCompany().getName().isEmpty());
    }

    @Test
    public void test_SIE4_file_with_errors() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_non_numeric_account_number.SE"));
        assertFalse("Logs should not be empty", result.getLogs().isEmpty());
        assertEquals("Should contain 1 critical error", 1l, result.getCriticals().size());
    }

    @Test
    public void test_SIE2_file_with_errors() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE2_UTF_8_with_errors.SE"));
        assertFalse("Logs should not be empty", result.getLogs().isEmpty());
        assertEquals("Should contain 4 warnings", 4l, result.getWarnings().size());
    }

    @Test
    public void test_account_number_lengths() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/Arousells_Visning_AB.SE"));
        long expectedNumberOfLogs = 5l;
        long expectedNumberOfWarnings = 3l;
        String expectedFirstMessage = "Kontonummer ska innehålla minst fyra siffror: 23";
        String expectedSecondMessage = "Kontot har fler än fyra siffror: 143010";
        assertFalse("Log list should not be empty", result.getLogs().isEmpty());
        assertEquals("Validator should contain " + expectedNumberOfLogs + " logs", expectedNumberOfLogs, result.getLogs().size());
        assertEquals("Validator should contain " + expectedNumberOfWarnings + " warnings", expectedNumberOfWarnings, result.getWarnings().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, result.getWarnings().get(0).getMessage());
        assertEquals("Fourth message should be " + expectedSecondMessage, expectedSecondMessage, result.getWarnings().get(1).getMessage());
    }

    @Test
    public void test_corrected_corporate_id() {
        String log1 = "SieLogDTO{level=INFO, message=Kontoplanstyp saknas, tag=null, origin=Document}";
        String log2 = "SieLogDTO{level=INFO, message=Organisationsnummer ska vara av formatet nnnnnn-nnnn. 5555555555, tag=#ORGNR, origin=Document}";
        String log3 = "SieLogDTO{level=INFO, message=Filer av typen I4 bör inte innehålla verifikationsnummer, tag=#VER, origin=Document}";
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/Transaktioner per Z-rapport.se"));
        List<String> logs = result.getLogs().stream().map(SieLogDTO::toString).collect(Collectors.toList());
        assertEquals("Should contain 3 logs", 3, logs.size());
        assertTrue("Should contain " + log1, logs.contains(log1));
        assertTrue("Should contain " + log2, logs.contains(log2));
        assertTrue("Should contain " + log3, logs.contains(log3));
    }

    @Test
    public void test_too_long_account_number() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_8_digit_account_number.SE"));
        long expectedNumberOfCriticalErrors = 1l;
        String error = "SieLogDTO{level=CRITICAL, message=Kontot är längre än sex siffror: 11100111, tag=null, origin=Sie4j}";
        assertEquals("Should contain " + expectedNumberOfCriticalErrors + " critical errors", expectedNumberOfCriticalErrors, result.getCriticals().size());
        assertTrue("Should contain " + error, result.getCriticals().stream().map(SieLogDTO::toString).collect(Collectors.toList()).contains(error));
    }

    @Test
    public void test_files() {
        ValidationResultDTO result1 = Sie4j.validate(asByteArray("/sample/Arousells_Visning_AB.SE"));
        long expectedNumberOfLogs = 5l;
        long expectedNumberOfWarnings = 3l;
        assertEquals("First validator should contain " + expectedNumberOfLogs + "  logs", expectedNumberOfLogs, result1.getLogs().size());
        assertEquals("First validator should contain " + expectedNumberOfWarnings + " warning", expectedNumberOfWarnings, result1.getWarnings().size());
        assertEquals("First validator should contain 2 info", 2l, result1.getInfos().size());
        ValidationResultDTO result2 = Sie4j.validate(asByteArray("/sample/Transaktioner per Z-rapport.se"));
        assertEquals("Second validator should contain 3 info logs", 3l, result2.getLogs().size());
    }

    @Test
    public void test_that_CorpoprateID_error_only_is_reported_once() {
        DataReader reader = Sie4j.readerFromSie(asByteArray("/sample/Testbolaget_Enskild_firma.SE"));
        assertEquals("Should contain 1 error", 1l, reader.validate().getLogs().size());
    }
    
    @Test
    public void test_that_CorpoprateID_error_reports_original_cid() {
        String expectedMessage = "Organisationsnummer ska vara av formatet nnnnnn-nnnn. 198605100000";
        DataReader reader = Sie4j.readerFromSie(asByteArray("/sample/Testbolaget_Enskild_firma.SE"));
        assertEquals("", expectedMessage, reader.validate().getLogs().get(0).getMessage());
    }

    private byte[] asByteArray(String path) {
        return SieReader.streamToByteArray(getClass().getResourceAsStream(path));
    }
}
