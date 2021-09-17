package sie.exception;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidSieTypeException extends SieException {

    public InvalidSieTypeException(String type) {
        super(type + " is not a valid SIE type");
    }

    public InvalidSieTypeException(String type, Throwable cause) {
        super(type + " is not a valid SIE type", cause);
    }
}
