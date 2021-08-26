package sie.exception;

import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 */
public class MissingVoucherDateException extends SieException {

    public MissingVoucherDateException() {
        super("Verifikationsdatum saknas", Entity.VOUCHER);
    }

}
