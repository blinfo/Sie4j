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
        if (entity.date() == null) {
            addCritical(VOUCHER, "Verifikationsdatum saknas!" + entity.optLine().map(l -> "\n " + l).orElse(""));
        }
        if (!entity.balanced()) {
            String message = "Verifikationen är i obalans. "
                    + entity.optLine().map(l -> "\n " + l + "\n ").orElse(entity.optSeries().map(s -> "Serie: " + s + ". ").orElse("")
                    + entity.optNumber().map(n -> "Nummer: " + n + ". ").orElse(""))
                    + "Differens: " + entity.diff();
            addCritical(VOUCHER, message);
        }
        if (entity.transactions().isEmpty()) {
            String message = "Verifikationen saknar transaktionsrader. "
                    + entity.optLine().map(l -> "\n " + l).orElse(entity.optSeries().map(s -> "Serie: " + s).orElse("")
                    + entity.optNumber().map(n -> " Nummer: " + n).orElse(""));
            addInfo(VOUCHER, message.trim());
        } else {
            entity.transactions().forEach(trans -> {
                addLogs(TransactionValidator.of(trans, type).getLogs());
            });
        }
    }
}
