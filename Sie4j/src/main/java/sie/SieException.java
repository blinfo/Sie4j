package sie;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
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
