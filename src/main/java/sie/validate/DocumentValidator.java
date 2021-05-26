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
    private final List<SieLog> logs;

    private DocumentValidator() {
        this(null, null);
    }

    private DocumentValidator(Document document, Document.Type type) {
        this.entity = document;
        this.type = type;
        this.logs = new ArrayList<>();
    }

    public static DocumentValidator from(Document document) {
        DocumentValidator documentValidator = new DocumentValidator(document, document.getMetaData().getSieType());
        documentValidator.validate();
        return documentValidator;
    }

    public static Validator of(SieException ex, Class origin) {
        DocumentValidator documentValidator = new DocumentValidator();
        SieLog.Builder builder = SieLog.builder().level(SieLog.Level.CRITICAL).message(ex.getMessage()).origin(origin);
        ex.getTag().ifPresent(builder::tag);
        documentValidator.addLog(builder.build());
        return documentValidator;
    }

    @Override
    public List<SieLog> getLogs() {
        return new ArrayList<>(logs);
    }

    private void validate() {
        logs.addAll(MetaDataValidator.from(entity.getMetaData()).getLogs());
        validateAccountingPlan();
        validateVouchers();
        validateBalances();
    }

    private void validateAccountingPlan() {
        if (!type.equals(Document.Type.I4) && !entity.getAccountingPlan().isPresent()) {
            addWarning("#KONTO", "Inga konton funna");
        } else {
            entity.getAccountingPlan().ifPresent(plan -> {
                addLogs(AccountingPlanValidator.of(plan, type).getLogs());
            });
        }
    }

    private void validateVouchers() {
        entity.getVouchers().forEach(voucher -> {
            addLogs(VoucherValidator.of(voucher, type).getLogs());
        });
    }

    private void addWarning(String tag, String message) {
        logs.add(SieLog.builder().origin(entity.getClass()).level(SieLog.Level.WARNING).tag(tag).message(message).build());
    }

    private void addLog(SieLog addLog) {
        logs.add(addLog);
    }

    private void addLogs(List<SieLog> logs) {
        this.logs.addAll(logs);
    }

    private void validateBalances() {
        addLogs(BalanceValidator.from(entity).getLogs());
    }
}
