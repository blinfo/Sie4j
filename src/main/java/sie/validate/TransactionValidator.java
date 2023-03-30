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
        if (entity.accountNumber() == null || entity.accountNumber().isEmpty()) {
            addCritical(TRANSACTION, "Kontonummer saknas!" + entity.optLine().map(l -> "\n " + l).orElse(""));
        }
        if (entity.amount() == null) {
            addCritical(TRANSACTION, "Belopp saknas!" + entity.optLine().map(l -> "\n " + l).orElse(""));
        }
    }
}
