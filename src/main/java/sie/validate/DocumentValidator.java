package sie.validate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sie.SieException;
import sie.domain.Document;
import sie.domain.Program;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidator implements Validator {

    private static final String MESSAGE = "stämmer inte med summering av verifikationerna";
    private final Document entity;
    private final LocalDateTime timestamp;
    private final List<SieLog> logs;

    private DocumentValidator() {
        this(null);
    }

    private DocumentValidator(Document document) {
        this.entity = document;
        this.timestamp = LocalDateTime.now();
        this.logs = new ArrayList<>();
    }

    public static DocumentValidator from(Document document) {
        DocumentValidator documentValidator = new DocumentValidator(document);
        documentValidator.validate();
        return documentValidator;
    }

    public static DocumentValidator of(SieException ex, Class origin) {
        DocumentValidator documentValidator = new DocumentValidator();
        SieLog.Builder builder = SieLog.builder().level(SieLog.Level.CRITICAL).message(ex.getMessage()).origin(origin);
        ex.getTag().ifPresent(builder::tag);
        documentValidator.addLog(builder.build());
        return documentValidator;
    }

    public boolean isValid() {
        return getCriticalErrors().isEmpty();
    }

    @Override
    public List<SieLog> getLogs() {
        return new ArrayList<>(logs);
    }

    public Optional<Document.Type> getType() {
        return Optional.of(entity).map(e -> e.getMetaData().getSieType());
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Optional<Program> getProgram() {
        return Optional.of(entity).map(e -> e.getMetaData().getProgram());
    }

    public Boolean hasResultBalanceVsVoucherImbalance() {
        return getLogs().stream()
                .filter(log -> log.getMessage().contains(MESSAGE))
                .findAny()
                .isPresent();
    }

    private void validate() {
        if (entity != null) {
            logs.addAll(MetaDataValidator.from(entity.getMetaData()).getLogs());
            validateAccountingPlan();
            validateVouchers();
            validateBalances();
        }
    }

    private void validateAccountingPlan() {
        getType().ifPresent(type -> {
            if (!type.equals(Document.Type.I4) && !entity.getAccountingPlan().isPresent()) {
                addWarning("#KONTO", "Inga konton funna");
            } else {
                entity.getAccountingPlan().ifPresent(plan -> {
                    addLogs(AccountingPlanValidator.of(plan, type).getLogs());
                });
            }
        });
    }

    private void validateVouchers() {
        getType().ifPresent(type -> {
            entity.getVouchers().forEach(voucher -> {
                addLogs(VoucherValidator.of(voucher, type).getLogs());
            });
        });
    }

    private void addWarning(String tag, String message) {
        logs.add(SieLog.builder().origin(entity.getClass()).level(SieLog.Level.WARNING).tag(tag).message(message).build());
    }

    private void addLog(SieLog addLog) {
        logs.add(addLog);
    }

    public void addLogs(List<SieLog> logs) {
        this.logs.addAll(logs);
    }

    private void validateBalances() {
        if (entity != null) {
            addLogs(BalanceValidator.from(entity).getLogs());
        }
    }
}
