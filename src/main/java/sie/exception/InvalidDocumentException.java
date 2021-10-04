package sie.exception;

import java.util.List;
import java.util.stream.Collectors;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidDocumentException extends SieException {

    public InvalidDocumentException(List<SieLog> criticalErrors) {
        super(criticalErrors.stream().map(SieLog::toString).collect(Collectors.joining("\n")));
    }

}
