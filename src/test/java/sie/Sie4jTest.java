package sie;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.validate.DocumentValidator;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 */
public class Sie4jTest {

    @Test
    public void test_file_with_missing_accounts() {
        DocumentValidator validator = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_numbers.SE"));
        List<SieLog> logs = validator.getLogs();
        long numberOfLogs = 1;
        SieLog.Level level = SieLog.Level.CRITICAL;
        String message = "Kontonummer får inte vara null eller tom sträng";
        assertEquals("Should contain" + numberOfLogs + " log", numberOfLogs, logs.size());
        SieLog log = logs.get(0);
        String origin = "Document";
        String tag = "#KONTO";
        assertEquals("Level should be " + level, level, log.getLevel());
        assertEquals("Message should be " + message, message, log.getMessage());
        assertTrue("Log should contain an origin", log.getOrigin().isPresent());
        assertEquals("Origin should be " + origin, origin, log.getOrigin().get());
        assertTrue("Log should contain a tag", log.getTag().isPresent());
        assertEquals("Tag should be " + tag, tag, log.getTag().get());
    }

    @Test
    public void test_file_with_missing_account_balance() {
        List<SieLog> logs = Sie4j.validate(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_account_balance.SE")).getLogs();
        long numberOfLogs = 1;
        SieLog.Level level = SieLog.Level.CRITICAL;
        String message = "Balansen för konto 1119 är inte ett tal";
        assertEquals("Should contain" + numberOfLogs + " log", numberOfLogs, logs.size());
        SieLog log = logs.get(0);
        String origin = "Document";
        String tag = "#KONTO";
        assertEquals("Level should be " + level, level, log.getLevel());
        assertEquals("Message should be " + message, message, log.getMessage());
        assertTrue("Log should contain an origin", log.getOrigin().isPresent());
        assertEquals("Origin should be " + origin, origin, log.getOrigin().get());
        assertTrue("Log should contain a tag", log.getTag().isPresent());
        assertEquals("Tag should be " + tag, tag, log.getTag().get());
    }

    @Test
    public void test_null_input() {
        String message = "Källan får inte vara null";
        DocumentValidator validator = Sie4j.validate(null);
        assertEquals("Validator should contain 1 log", 1l, validator.getLogs().size());
        assertEquals("Validator should contain 1 critical error", 1l, validator.getCriticalErrors().size());
        SieLog critical = validator.getCriticalErrors().get(0);
        assertEquals("Message should be " + message, message, critical.getMessage());
        assertFalse("Log contains no tag", critical.getTag().isPresent());
    }

    @Test
    public void test_stream_throws_IOException() {
        InputStream stream = new ByteArrayInputStream("THROW".getBytes());
        String message = "Kunde inte läsa källan";
        DocumentValidator validator = Sie4j.validate(stream);
        assertEquals("Validator should contain 1 log", 1l, validator.getLogs().size());
        assertEquals("Validator should contain 1 critical error", 1l, validator.getCriticalErrors().size());
        SieLog critical = validator.getCriticalErrors().get(0);
        assertEquals("Message should be " + message, message, critical.getMessage());
        assertFalse("Log contains no tag", critical.getTag().isPresent());
    }

}
