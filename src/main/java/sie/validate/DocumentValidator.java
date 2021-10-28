package sie.validate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sie.domain.Document;
import sie.domain.Program;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidator implements Validator {

    private static final String MESSAGE = "stämmer inte med summering av verifikationerna";
    private final Document entity;
    private final LocalDateTime timestamp;
    private final List<SieLog> logs;
    private final Boolean checkBalances;

    private DocumentValidator() {
        this(null, null);
    }

    private DocumentValidator(Document document, Boolean checkBalances) {
        this.entity = document;
        this.timestamp = LocalDateTime.now();
        this.logs = new ArrayList<>();
        this.checkBalances = checkBalances;
    }

    public static DocumentValidator from(Document document) {
        return of(document, Boolean.FALSE);
    }

    public static DocumentValidator of(Document document, Boolean checkBalances) {
        DocumentValidator documentValidator = new DocumentValidator(document, checkBalances);
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

    /**
     * Getter for SieLogs.
     * <p>
     * Only unique SieLogs are returned. If an error occurs several times in the
     * input, only one will be logged.
     *
     * @return List of unique SieLogs
     */
    @Override
    public List<SieLog> getLogs() {
        return logs.stream().sorted().collect(Collectors.toList());
    }

    public Optional<Document.Type> getType() {
        return Optional.ofNullable(entity).map(e -> e.getMetaData().getSieType());
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Optional<Program> getProgram() {
        return Optional.ofNullable(entity).map(e -> e.getMetaData().getProgram());
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
            if (checkBalances) {
                validateBalances();
            }
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
