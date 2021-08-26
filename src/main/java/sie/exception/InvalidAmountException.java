package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidAmountException extends SieException {

    public InvalidAmountException(String message, Throwable cause, String tag) {
        super(message, cause, tag);
    }

}
