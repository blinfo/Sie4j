package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidTransactionDataException extends MalformedLineException {

    public InvalidTransactionDataException(String line) {
        super(line, "TRANS");
    }

}
