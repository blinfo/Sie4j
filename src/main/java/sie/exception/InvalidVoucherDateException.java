package sie.exception;

import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidVoucherDateException extends SieException {

    public InvalidVoucherDateException(String dateString, Throwable e) {
        super("Kan inte läsa verifikationsdatum: \"" + dateString + "\"", e, Entity.VOUCHER);
    }

    
}
