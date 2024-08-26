package sie.exception;

import java.util.Optional;

/**
 *
 * @author Håkan Lidén
 *
 */
public class SieException extends RuntimeException {
    
    private final String tag;
    
    public SieException(String message) {
        super(message);
        this.tag = null;
    }
    
    public SieException(String message, String tag) {
        super(message);
        this.tag = tag;
    }
    
    public SieException(String message, Throwable cause) {
        super(message, cause);
        this.tag = null;
    }
    
    public SieException(String message, Throwable cause, String tag) {
        super(message, cause);
        this.tag = tag;
    }
    
    public SieException(Throwable cause) {
        super(cause);
        this.tag = null;
    }
    
    public SieException(Throwable cause, String tag) {
        super(cause);
        this.tag = tag;
    }
    
    public Optional<String> getTag() {
        return Optional.ofNullable(tag).map(t -> {
            if (t.startsWith("#")) {
                return t;
            }
            return "#" + t;
        });
    }
}
