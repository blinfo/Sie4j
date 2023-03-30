package sie.exception;

import java.util.List;
import java.util.stream.Collectors;
import sie.log.SieLog;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidDocumentException extends SieException {

    public InvalidDocumentException(String message) {
        super(message);
    }

    public InvalidDocumentException(List<SieLog> criticalErrors) {
        super(criticalErrors.stream().map(SieLog::toString).collect(Collectors.joining("\n")));
    }

}
