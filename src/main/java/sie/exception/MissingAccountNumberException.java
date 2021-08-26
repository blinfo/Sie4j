package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class MissingAccountNumberException extends SieException {

    public MissingAccountNumberException(String tag) {
        super("Kontonummer saknas", tag);
    }

}
