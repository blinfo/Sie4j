package sie.exception;


/**
 *
 * @author Håkan Lidén
 */
public class MissingAccountNumberAndAmountException extends SieException {

    public MissingAccountNumberAndAmountException(String tag) {
        super("Kontonummer och belopp saknas", tag);
    }

}
