package sie;

import sie.dto.SieLogDTO;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.domain.*;
import sie.dto.*;
import sie.exception.SieException;
import sie.log.SieLog;


/**
 *
 * @author Håkan Lidén
 */
public class Sie4jTest {

    @Test
    public void test_file_with_missing_accounts() {
        ValidationResultDTO validate = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        List<SieLogDTO> logs = validate.criticals();
        long numberOfCriticals = 1;
        String level = SieLog.Level.CRITICAL.name();
        String message = "Kontonummer saknas";
        assertEquals(numberOfCriticals, logs.size());
        SieLogDTO log = logs.get(0);
        String origin = Sie4j.class.getSimpleName();
        assertEquals(level, log.getLevel());
        assertEquals(message, log.getMessage());
        assertEquals(origin, log.getOrigin());
    }

    @Test
    public void test_file_with_missing_account_balance() {
        List<SieLogDTO> logs = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_balance.SE")).logs();
        long numberOfLogs = 1;
        String level = SieLog.Level.CRITICAL.name();
        String message = "Strängen '' för balans, konto 1119, kan inte hanteras som belopp\n #IB 0 1119 \"\"";
        assertEquals(numberOfLogs, logs.size());
        SieLogDTO log = logs.get(0);
        String origin = Sie4j.class.getSimpleName();
        assertEquals(level, log.getLevel());
        assertEquals(message, log.getMessage());
        assertEquals(origin, log.getOrigin());
    }

    @Test
    public void test_null_input() {
        String message = "Källan får inte vara null";
        InputStream input = null;
        SieException ex = assertThrows(SieException.class, () -> SieReader.streamToByteArray(input));
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void test_stream_throws_IOException() {
        String message = "Kunde inte läsa källan";
        ValidationResultDTO validator = Sie4j.validate("THROW".getBytes());
        assertEquals(1l, validator.logs().size());
        assertEquals(1l, validator.criticals().size());
        SieLogDTO critical = validator.criticals().get(0);
        assertEquals(message, critical.getMessage());
        assertTrue(critical.getTag() == null);
    }

    @Test
    public void test_write_sie_file() {
        Document document = Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE1.SE"));
        File target = new File(System.getProperty("user.home") + "/sie-test/BLBLOV_SIE1_copy.SE");
        assertFalse(target.exists());
        target.getParentFile().mkdirs();
        Sie4j.asSie(document, target);
        assertTrue(target.exists());
        target.delete();
        assertFalse(target.exists());
    }

    @Test
    public void test_file_with_missing_voucher_series_marker() {
        // If no series is given, an empty string should be used in the SIE file:
        // #VER "" 1 20170101 " " 20170119 " " 
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_voucher_series.SE"));
        Voucher firstVoucher = doc.vouchers().get(0);
        assertEquals(1l, firstVoucher.optNumber().get().longValue());
        assertFalse(firstVoucher.optSeries().isPresent());
        assertFalse(firstVoucher.optText().isPresent());
        assertFalse(firstVoucher.optSignature().isPresent());
    }

    @Test
    public void test_SIE2_with_non_numeric_account_number_should_throw_exception() {
        SieException ex = assertThrows(SieException.class, () -> Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE2_UTF_8_with_non_numeric_account_number.SE")));
        String expectedMessage = "Kontot har inte ett numeriskt värde: 11AF";
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void test_that_too_long_account_number_throws_exception() {
        SieException ex = assertThrows(SieException.class, () -> Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_8_digit_account_number.SE")));
        String expectedMessage = "Kontot är längre än sex siffror: 11100111\n #KONTO 11100111 \"Byggnader\"";
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void test_validate_file_with_missing_company_name() {
        ValidationResultDTO validator = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_company_name.SI"));
        String expectedMessage = "Företagsnamn saknas";
        assertEquals(1l, validator.warnings().size());
        assertEquals(expectedMessage, validator.warnings().get(0).getMessage());
    }

    @Test
    public void test_file_with_missing_company_name() {
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_company_name.SI"));
        assertTrue(doc.metaData().getCompany().name().isEmpty());
    }

    @Test
    public void test_SIE4_file_with_errors() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_non_numeric_account_number.SE"));
        assertFalse(result.logs().isEmpty());
        assertEquals(1l, result.criticals().size());
    }

    @Test
    public void test_SIE2_file_with_errors() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE2_UTF_8_with_errors.SE"));
        assertFalse(result.logs().isEmpty());
        assertEquals(4l, result.warnings().size());
    }

    @Test
    public void test_account_number_lengths() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/Arousells_Visning_AB.SE"));
        long expectedNumberOfLogs = 5l;
        long expectedNumberOfWarnings = 3l;
        String expectedFirstMessage = "Kontonummer ska innehålla minst fyra siffror: 23";
        String expectedSecondMessage = "Kontot har fler än fyra siffror: 143010";
        assertFalse(result.logs().isEmpty());
        assertEquals(expectedNumberOfLogs, result.logs().size());
        assertEquals(expectedNumberOfWarnings, result.warnings().size());
        assertEquals(expectedFirstMessage, result.warnings().get(0).getMessage());
        assertEquals(expectedSecondMessage, result.warnings().get(1).getMessage());
    }

    @Test
    public void test_corrected_corporate_id() {
        String log1 = "SieLogDTO{level=INFO, message=Kontoplanstyp saknas, tag=null, origin=Document, line=null}";
        String log2 = "SieLogDTO{level=INFO, message=Organisationsnummer ska vara av formatet nnnnnn-nnnn. 5555555555, tag=#ORGNR, origin=Document, line=#ORGNR 5555555555}";
        String log3 = "SieLogDTO{level=INFO, message=Filer av typen I4 bör inte innehålla verifikationsnummer, tag=#VER, origin=Document, line=#VER \"\" \"64\" 20210505 \" nr. 64\" 20210505  }";
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/Transaktioner per Z-rapport.se"));
        List<String> logs = result.logs().stream().map(SieLogDTO::toString).collect(Collectors.toList());
        assertEquals(12, logs.size());
        assertTrue(logs.contains(log1));
        assertTrue(logs.contains(log2));
        assertTrue(logs.contains(log3));
    }

    @Test
    public void test_too_long_account_number() {
        ValidationResultDTO result = Sie4j.validate(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_8_digit_account_number.SE"));
        long expectedNumberOfCriticalErrors = 1l;
//        String error = "SieLogDTO{level=CRITICAL, message=Kontot är längre än sex siffror: 11100111, tag=#KONTO, origin=Sie4j, line=#KONTO 11100111 \"Byggnader\"}";
        String error = "SieLogDTO{level=CRITICAL, message=Kontot är längre än sex siffror: 11100111\n #KONTO 11100111 \"Byggnader\", tag=#KONTO, origin=Sie4j, line=null}";
        assertEquals(expectedNumberOfCriticalErrors, result.criticals().size());
        assertTrue(result.criticals().stream().map(SieLogDTO::toString).collect(Collectors.toList()).contains(error));
    }

    @Test
    public void test_files() {
        ValidationResultDTO result1 = Sie4j.validate(asByteArray("/sample/Arousells_Visning_AB.SE"));
        long expectedNumberOfLogs = 5l;
        long expectedNumberOfWarnings = 3l;
        assertEquals(expectedNumberOfLogs, result1.logs().size());
        assertEquals(expectedNumberOfWarnings, result1.warnings().size());
        assertEquals(2l, result1.infos().size());
        ValidationResultDTO result2 = Sie4j.validate(asByteArray("/sample/Transaktioner per Z-rapport.se"));
        assertEquals(12l, result2.logs().size());
    }

    @Test
    public void test_that_CorpoprateID_error_only_is_reported_once() {
        DataReader reader = Sie4j.readerFromSie(asByteArray("/sample/Testbolaget_Enskild_firma.SE"));
        assertEquals(1l, reader.validate().getLogs().size());
    }

    @Test
    public void test_that_CorpoprateID_error_reports_original_cid() {
        String expectedMessage = "Organisationsnummer ska vara av formatet nnnnnn-nnnn. 198605100000";
        DataReader reader = Sie4j.readerFromSie(asByteArray("/sample/Testbolaget_Enskild_firma.SE"));
        assertEquals(expectedMessage, reader.validate().getLogs().get(0).getMessage());
    }

    @Test
    public void test_project_costBearer_and_costCenter_with_long_numbers_and_labels() {
        DataReader reader = Sie4j.readerFromSie(asByteArray("/sample/SIE_test_av_KST_projekt_Testbolaget_e - kopia.SE"));
        String expectedLabel = "TEST alldeles for langt projektnamn fungerar detta att importera";
        String expectedNumber = "1234567891234567999";
        assertTrue(reader.read().objects().stream().map(ao -> ao.label()).anyMatch(s -> s.equals(expectedLabel)));
        assertTrue(reader.read().objects().stream().map(ao -> ao.number()).anyMatch(s -> s.equals(expectedNumber)));
    }

    @Test
    public void test2_project_costBearer_and_costCenter_with_long_numbers_and_labels() {
        String expectedLabel = "ASDF XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        DataReader reader = Sie4j.readerFromSie(asByteArray("/sample/SIE_test_av_KST_projekt_Testbolaget_e - kopia ny.SE"));
        assertTrue(reader.read().objects().stream().map(ao -> ao.label()).anyMatch(s -> s.equals(expectedLabel)));
    }

    private byte[] asByteArray(String path) {
        return SieReader.streamToByteArray(getClass().getResourceAsStream(path));
    }
}
