package sie;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class SieException extends RuntimeException {

    public SieException(String message) {
        super(message);
    }

    public SieException(String message, Throwable cause) {
        super(message, cause);
    }

    public SieException(Throwable cause) {
        super(cause);
    }

}
