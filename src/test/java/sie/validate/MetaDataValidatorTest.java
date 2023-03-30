package sie.validate;

import sie.log.SieLog;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class MetaDataValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_faulty_address() {
        Document document = getDocument("CC3.SI");
        List<String> logs = MetaDataValidator.from(document.getMetaData()).getLogs().stream().map(SieLog::toString).collect(Collectors.toList());
        String log1 = "SieLog{origin=MetaData, level=INFO, tag=#ADRESS, message=Kontaktperson saknas i adress\n"
                + " #ADRESS \"\" \"Möllevägen 5\" \"82450 Hudiksvall\" \"\"}";
        String log2 = "SieLog{origin=MetaData, level=INFO, tag=#ADRESS, message=Telefonnummer saknas i adress\n"
                + " #ADRESS \"\" \"Möllevägen 5\" \"82450 Hudiksvall\" \"\"}";
        assertEquals("Should contain 2 logs", 2, logs.size());
        assertEquals("Log 1 should be " + log1, log1, logs.get(0));
        assertEquals("Log 2 should be " + log2, log2, logs.get(1));
    }

    @Test
    public void test_faulty_program() {
        Document document = getDocument("SIE_with_missing_program_version.se");
        String result = MetaDataValidator.from(document.getMetaData()).getLogs().get(0).toString();
        String log = "SieLog{origin=MetaData, level=INFO, tag=#PROGRAM, message=Programversion saknas\n"
                + " #PROGRAM SIR}";
        assertEquals("Log should be " + log, log, result);
    }

    @Test
    public void test_faulty_range() {
        Document document = getDocument("BLBLOV_SIE3.SE");
        String result = MetaDataValidator.from(document.getMetaData()).getLogs().get(0).toString();
        String log = "SieLog{origin=MetaData, level=WARNING, tag=#OMFATTN, message=Omfattning saknas}";
        assertEquals("Log should be " + log, log, result);
    }
}
