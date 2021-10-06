package sie.validate;

import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.Sie4j;
import sie.exception.SieException;
import sie.domain.Document;
import sie.dto.SieLogDTO;
import sie.dto.ValidationResultDTO;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_SIE4_file_with_errors() {
        ValidationResultDTO result = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_non_numeric_account_number.SE"));
        assertFalse("Logs should not be empty", result.getLogs().isEmpty());
        assertEquals("Should contain 1 critical error", 1l, result.getCriticals().size());
    }

    @Test
    public void test_SIE2_file_with_errors() {
        ValidationResultDTO result = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE2_UTF_8_with_errors.SE"));
        assertFalse("Logs should not be empty", result.getLogs().isEmpty());
        assertEquals("Should contain 4 warnings", 4l, result.getWarnings().size());
    }

    @Test
    public void test_validator_from_SieException() {
        String message = "Taggen får inte förekomma i SIE-formatet";
        String tag = "#TEST";
        Class origin = Document.class;
        SieLog.Level level = SieLog.Level.CRITICAL;
        DocumentValidator validator = DocumentValidator.of(new SieException(message, tag), origin);
        assertEquals("Validator should contain 1 log", 1l, validator.getLogs().size());
        assertEquals("Validator should contain 1 critical error", 1l, validator.getCriticalErrors().size());
        SieLog critical = validator.getCriticalErrors().get(0);
        assertEquals("Message should be " + message, message, critical.getMessage());
        assertEquals("Level should be " + level, level, critical.getLevel());
        assertTrue("Log should contain origin", critical.getOrigin().isPresent());
        assertEquals("Origin should be " + origin.getSimpleName(), origin.getSimpleName(), critical.getOrigin().get());
        assertTrue("Log should contain tag", critical.getTag().isPresent());
        assertEquals("Tag should be " + tag, tag, critical.getTag().get());
    }

    @Test
    public void test_balances_and_results_against_vouchers() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        long expectedNumberOfWarnings = 82;
        String expectedFirstMessage = "Resultat för konto 3001 år 0 stämmer inte med summering av verifikationerna Resultat: -25035.36 Summa: 0.00";
        assertTrue("Log list should not be empty", validator.getLogs().size() > 0);
        assertTrue("Validator should show imbalance", validator.hasResultBalanceVsVoucherImbalance());
        assertEquals("Validator should contain " + expectedNumberOfWarnings + " warnings", expectedNumberOfWarnings, validator.getWarnings().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, validator.getWarnings().get(0).getMessage());
    }

    @Test
    public void test_document_validator_balances_and_results_against_vouchers() {
        ValidationResultDTO result = Sie4j.validate(getClass().getResourceAsStream("/sample/Arousells_Visning_AB.SE"));
        long expectedNumberOfLogs = 87;
        long expectedNumberOfWarnings = 85;
        String expectedFirstMessage = "Kontonummer ska innehålla minst fyra siffror: 23";
        String expectedFourthMessage = "Resultat för konto 3001 år 0 stämmer inte med summering av verifikationerna Resultat: -25035.36 Summa: 0.00";
        assertFalse("Log list should not be empty", result.getLogs().isEmpty());
        assertEquals("Validator should contain " + expectedNumberOfLogs + " logs", expectedNumberOfLogs, result.getLogs().size());
        assertEquals("Validator should contain " + expectedNumberOfWarnings + " warnings", expectedNumberOfWarnings, result.getWarnings().size());
        assertEquals("First message should be " + expectedFirstMessage, expectedFirstMessage, result.getWarnings().get(0).getMessage());
        assertEquals("Fourth message should be " + expectedFourthMessage, expectedFourthMessage, result.getWarnings().get(3).getMessage());
    }

    @Test
    public void test_getProgram() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        String expectedName = "BL Administration";
        String expectedVersion = "2021.2.103";
        assertTrue("Should have a program", validator.getProgram().isPresent());
        assertEquals("Name should be " + expectedName, expectedName, validator.getProgram().get().getName());
        assertEquals("Version should be " + expectedVersion, expectedVersion, validator.getProgram().get().getVersion());
    }

    @Test
    public void test_faulty_program() {
        Document document = getDocument("SIE_with_missing_program_version.se");
        DocumentValidator validator = DocumentValidator.from(document);
        SieLog log = validator.getLogs().get(0);
        String expectedLog = "SieLog{origin=MetaData, level=WARNING, tag=#PROGRAM, message=Programversion saknas}";
        assertEquals("SieLog should be " + expectedLog, expectedLog, log.toString());
    }

    @Test
    public void test_corrected_corporate_id() {
        String log1 = "SieLogDTO{level=INFO, message=Kontoplanstyp saknas, tag=null, origin=Document}";
        String log2 = "SieLogDTO{level=INFO, message=Organisationsnummer ska vara av formatet nnnnnn-nnnn. 5555555555, tag=#ORGNR, origin=Document}";
        String log3 = "SieLogDTO{level=INFO, message=Filer av typen I4 bör inte innehålla verifikationsnummer, tag=#VER, origin=Document}";
        ValidationResultDTO result = Sie4j.validate(getClass().getResourceAsStream("/sample/Transaktioner per Z-rapport.se"));
        List<String> logs = result.getLogs().stream().map(SieLogDTO::toString).collect(Collectors.toList());
        assertEquals("Should contain 3 logs", 3, logs.size());
        assertTrue("Should contain " + log1, logs.contains(log1));
        assertTrue("Should contain " + log2, logs.contains(log2));
        assertTrue("Should contain " + log3, logs.contains(log3));
    }

    @Test
    public void test_too_long_account_number() {
        ValidationResultDTO result = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_8_digit_account_number.SE"));
        long expectedNumberOfCriticalErrors = 1l;
        String error = "SieLogDTO{level=CRITICAL, message=Kontot är längre än sex siffror: 11100111, tag=#KONTO, origin=DocumentFactory}";
        assertEquals("Should contain " + expectedNumberOfCriticalErrors + " critical errors", expectedNumberOfCriticalErrors, result.getCriticals().size());
        assertTrue("Should contain " + error, result.getCriticals().stream().map(SieLogDTO::toString).collect(Collectors.toList()).contains(error));
    }
}
