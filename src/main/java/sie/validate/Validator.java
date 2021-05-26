package sie.validate;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén
 */
public interface Validator {

    List<SieLog> getLogs();

    default List<SieLog> getCriticalErrors() {
        return getLogs().stream().filter(log -> log.getLevel().equals(SieLog.Level.CRITICAL)).collect(Collectors.toList());
    }

    default List<SieLog> getWarnings() {
        return getLogs().stream().filter(log -> log.getLevel().equals(SieLog.Level.WARNING)).collect(Collectors.toList());
    }

    default List<SieLog> getInfo() {
        return getLogs().stream().filter(log -> log.getLevel().equals(SieLog.Level.INFO)).collect(Collectors.toList());
    }
}
