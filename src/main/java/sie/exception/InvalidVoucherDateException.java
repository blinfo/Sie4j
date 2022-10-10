package sie.exception;

import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 */
public class InvalidVoucherDateException extends SieException {

    public InvalidVoucherDateException(String dateString, String line, Throwable e) {
        super("Kan inte läsa verifikationsdatum: '" + dateString + "'" + "\n " + line, e, Entity.VOUCHER);
    }

    
}
