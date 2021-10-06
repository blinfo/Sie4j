package sie.validate;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import sie.domain.Document;
import sie.domain.Voucher;

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
            addCritical(VOUCHER, "Verifikationsdatum saknas!");
        }
        if (!entity.getDiff().equals(BigDecimal.ZERO)) {
            String message = "Verifikationen är i obalans. "
                    + entity.getSeries().map(s -> "Serie: " + s + ". ").orElse("")
                    + entity.getNumber().map(n -> "Nummer: " + n + ". ").orElse("")
                    + "Datum: " + entity.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ". "
                    + "Differens: " + entity.getDiff();
            addCritical(VOUCHER, message);
        }
        if (entity.getTransactions().isEmpty()) {
            String message = "Verifikationen innehåller inga transaktionsrader"
                    + entity.getSeries().map(s -> " Serie: " + s).orElse("")
                    + entity.getNumber().map(n -> " Nummer: " + n).orElse("");
            addInfo(VOUCHER, message.trim());
        } else {
            entity.getTransactions().forEach(trans -> {
                addLogs(TransactionValidator.of(trans, type).getLogs());
            });
        }
    }
}
