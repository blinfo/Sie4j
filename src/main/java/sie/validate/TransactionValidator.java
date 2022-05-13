package sie.validate;

import sie.domain.*;

/**
 *
 * @author Håkan Lidén
 */
class TransactionValidator extends AbstractValidator<Transaction> {

    private static final String TRANSACTION = "#TRANS";

    private TransactionValidator(Transaction entity, Document.Type type) {
        super(entity, type);
    }

    public static TransactionValidator of(Transaction entity, Document.Type type) {
        return new TransactionValidator(entity, type);
    }

    @Override
    protected void validate() {
        if (entity.getAccountNumber() == null || entity.getAccountNumber().isEmpty()) {
            addCritical(TRANSACTION, "Kontonummer saknas!");
        }
        if (entity.getAmount() == null) {
            addCritical(TRANSACTION, "Belopp saknas!");
        }
    }
}
