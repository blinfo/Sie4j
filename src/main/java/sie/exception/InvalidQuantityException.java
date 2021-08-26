package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidQuantityException extends SieException {

    public InvalidQuantityException(String message, Throwable cause, String tag) {
        super(message, cause, tag);
    }

}
