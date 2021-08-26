package sie.exception;

import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 */
public class AccountNumberException extends SieException {

    public AccountNumberException(String message) {
        super(message, Entity.ACCOUNT);
    }

    public AccountNumberException(String message, Throwable ex) {
        super(message, ex, Entity.ACCOUNT);
    }

}
