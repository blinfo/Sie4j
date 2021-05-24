package sie.validate;

import java.math.BigDecimal;
import sie.domain.Document;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
class VoucherValidator extends AbstractValidator<Voucher> {

    private VoucherValidator(Voucher entity, Document.Type type) {
        super(entity, type);
    }

    public static VoucherValidator of(Voucher entity, Document.Type type) {
        return new VoucherValidator(entity, type);
    }

    @Override
    protected void validate() {
        if (Integer.valueOf(type.getNumber()) < 4) {
            addFatal("#VER", "Files of type " + type.getNumber() + " must not contain vouchers");
            return;
        }
        if (entity.getDate() == null) {
            addFatal("#VER", "Voucher date is missing");
        }
        if (!entity.getDiff().equals(BigDecimal.ZERO)) {
            String message = "Voucher is not balanced. "
                    + entity.getSeries().map(s -> "Series: " + s + " ").orElse("")
                    + entity.getNumber().map(n -> "Number: " + n + " ").orElse("")
                    + "Difference: " + entity.getDiff();
            addFatal("#VER", message);
        }
        if (entity.getTransactions().isEmpty()) {
            String message = "Voucher does not contain any transactions. "
                    + entity.getSeries().map(s -> "Series: " + s + " ").orElse("")
                    + entity.getNumber().map(n -> "Number: " + n + " ").orElse("");
            addInfo("#VER", message.trim());
        } else {
            entity.getTransactions().forEach(trans -> {
                addErrors(TransactionValidator.of(trans, type).getErrors());
            });
        }
    }
}
