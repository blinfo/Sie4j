package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class MissingAmountException extends SieException {

    public MissingAmountException(String tag) {
        super("Belopp saknas", tag);
    }

}
