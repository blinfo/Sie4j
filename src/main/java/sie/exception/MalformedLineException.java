package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class MalformedLineException extends SieException {

    public MalformedLineException(String line, String tag) {
        super("Malformed line. '" + line + "'", tag);
    }

    
}
