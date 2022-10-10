package sie.validate;

import sie.domain.*;

/**
 *
 * @author Håkan Lidén
 */
class VoucherValidator extends AbstractValidator<Voucher> {

    private static final String VOUCHER = "#VER";

    private VoucherValidator(Voucher entity, Document.Type type) {
        super(entity, type);
    }

    public static VoucherValidator of(Voucher entity, Document.Type type) {
        return new VoucherValidator(entity, type);
    }

    @Override
    protected void validate() {
        if (entity.getDate() == null) {
            addCritical(VOUCHER, "Verifikationsdatum saknas!" + entity.getLine().map(l -> "\n " + l).orElse(""));
        }
        if (!entity.isBalanced()) {
            String message = "Verifikationen är i obalans. "
                    + entity.getLine().map(l -> "\n " + l + "\n ").orElse(entity.getSeries().map(s -> "Serie: " + s + ". ").orElse("")
                    + entity.getNumber().map(n -> "Nummer: " + n + ". ").orElse(""))
                    + "Differens: " + entity.getDiff();
            addCritical(VOUCHER, message);
        }
        if (entity.getTransactions().isEmpty()) {
            String message = "Verifikationen saknar transaktionsrader. "
                    + entity.getLine().map(l -> "\n " + l).orElse(entity.getSeries().map(s -> "Serie: " + s).orElse("")
                    + entity.getNumber().map(n -> " Nummer: " + n).orElse(""));
            addInfo(VOUCHER, message.trim());
        } else {
            entity.getTransactions().forEach(trans -> {
                addLogs(TransactionValidator.of(trans, type).getLogs());
            });
        }
    }
}
