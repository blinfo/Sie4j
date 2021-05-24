package sie.validate;

import java.util.ArrayList;
import java.util.List;
import sie.SieException;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidator implements Validator {

    private final Document entity;
    private final Document.Type type;
    private final List<SieError> errors;

    private DocumentValidator() {
        this(null, null);
    }

    private DocumentValidator(Document document, Document.Type type) {
        this.entity = document;
        this.type = type;
        this.errors = new ArrayList<>();
    }

    public static DocumentValidator from(Document document) {
        DocumentValidator documentValidator = new DocumentValidator(document, document.getMetaData().getSieType());
        documentValidator.validate();
        return documentValidator;
    }

    public static Validator of(SieException ex, Class origin) {
        DocumentValidator documentValidator = new DocumentValidator();
        SieError.Builder builder = SieError.builder().level(SieError.Level.FATAL).message(ex.getMessage()).origin(origin);
        ex.getTag().ifPresent(builder::tag);
        documentValidator.addError(builder.build());
        return documentValidator;
    }

    @Override
    public List<SieError> getErrors() {
        return new ArrayList<>(errors);
    }

    private void validate() {
        errors.addAll(MetaDataValidator.from(entity.getMetaData()).getErrors());
        validateAccountingPlan();
        validateVouchers();
        validateBalances();
    }

    private void validateAccountingPlan() {
        if (!type.equals(Document.Type.I4) && !entity.getAccountingPlan().isPresent()) {
            addWarning("#KONTO", "Inga konton funna");
        } else {
            entity.getAccountingPlan().ifPresent(plan -> {
                addErrors(AccountingPlanValidator.of(plan, type).getErrors());
            });
        }
    }

    private void validateVouchers() {
        entity.getVouchers().forEach(voucher -> {
            addErrors(VoucherValidator.of(voucher, type).getErrors());
        });
    }

    private void addWarning(String tag, String message) {
        errors.add(SieError.builder().origin(entity.getClass()).level(SieError.Level.WARNING).tag(tag).message(message).build());
    }

    private void addError(SieError error) {
        errors.add(error);
    }

    private void addErrors(List<SieError> errors) {
        this.errors.addAll(errors);
    }

    private void validateBalances() {
        addErrors(BalanceValidator.from(entity).getErrors());
    }
}
