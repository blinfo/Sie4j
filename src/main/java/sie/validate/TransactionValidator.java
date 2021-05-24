package sie.validate;

import sie.domain.Document;
import sie.domain.Transaction;

/**
 *
 * @author Håkan Lidén
 */
public class TransactionValidator extends AbstractValidator<Transaction> {

    private TransactionValidator(Transaction entity, Document.Type type) {
        super(entity, type);
    }

    public static TransactionValidator of(Transaction entity, Document.Type type) {
        return new TransactionValidator(entity, type);
    }

    @Override
    protected void validate() {
        if (entity.getAccountNumber() == null || entity.getAccountNumber().isEmpty()) {
            addFatal("#TRANS", "Account number is missing");
        }
        if (entity.getAmount() == null) {
            addFatal("#TRANS", "Amount is missing");
        }
    }

}
