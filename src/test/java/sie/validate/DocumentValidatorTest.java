package sie.validate;

import sie.log.SieLog;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.domain.Document;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_validator_from_SieException() {
        String message = "Taggen får inte förekomma i SIE-formatet";
        String tag = "#TEST";
        Class origin = Document.class;
        SieLog.Level level = SieLog.Level.CRITICAL;
        DocumentValidator validator = DocumentValidator.of(new SieException(message, tag), origin);
        assertEquals(1l, validator.getLogs().size());
        assertEquals(1l, validator.getCriticalErrors().size());
        SieLog critical = validator.getCriticalErrors().get(0);
        assertEquals(message, critical.getMessage());
        assertEquals(level, critical.getLevel());
        assertTrue(critical.getOrigin().isPresent());
        assertEquals(origin.getSimpleName(), critical.getOrigin().get());
        assertTrue(critical.getTag().isPresent());
        assertEquals(tag, critical.getTag().get());
    }

    @Test
    public void test_balances_and_results_against_vouchers() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        DocumentValidator validator = DocumentValidator.of(document, Boolean.TRUE);
        long expectedNumberOfWarnings = 62;
        String expectedFirstMessage = """
                                      Resultat f\u00f6r konto 3001 \u00e5r 0 st\u00e4mmer inte med summering av verifikationerna
                                       Resultat: -25035.36 Summa: 0.00""";
        String expectedFirstLine = "#RES 0 3001 -25035.36";
        assertTrue(validator.getLogs().size() > 0);
        assertTrue(validator.hasResultBalanceVsVoucherImbalance());
        assertEquals(expectedNumberOfWarnings, validator.getWarnings().size());
        assertEquals(expectedFirstMessage, validator.getWarnings().get(0).getMessage());
        assertEquals(expectedFirstLine, validator.getWarnings().get(0).getLine().orElse(""));
    }

    @Test
    public void test_getProgram() {
        Document document = getDocument("Arousells_Visning_AB.SE");
        DocumentValidator validator = DocumentValidator.from(document);
        String expectedName = "BL Administration";
        String expectedVersion = "2021.2.103";
        assertTrue(validator.getProgram().isPresent());
        assertEquals(expectedName, validator.getProgram().get().name());
        assertEquals(expectedVersion, validator.getProgram().get().version());
    }

    @Test
    public void test_faulty_program() {
        Document document = getDocument("SIE_with_missing_program_version.se");
        DocumentValidator validator = DocumentValidator.from(document);
        SieLog log = validator.getLogs().get(0);
        String expectedLog = """
                             SieLog{origin=MetaData, level=INFO, tag=#PROGRAM, message=Programversion saknas
                              #PROGRAM SIR}""";
        assertEquals(expectedLog, log.toString());
    }
}
