package sie.validate;

import sie.log.SieLog;
import java.util.*;
import java.util.stream.Collectors;
import sie.domain.*;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 * @param <T>
 */
abstract class AbstractValidator<T extends Entity> implements Validator {

    protected final T entity;
    protected final Document.Type type;
    private final List<SieLog> logs;

    public AbstractValidator(T entity, Document.Type type) {
        this.entity = entity;
        this.type = type;
        logs = new ArrayList<>();
        init();
    }

    protected void addLogs(List<SieLog> logs) {
        this.logs.addAll(logs);
    }

    protected void addInfo(String tag, String message) {
        logs.add(SieLog.builder().origin(entity.getClass()).level(SieLog.Level.INFO).tag(tag).message(message).build());
    }

    protected void addWarning(String tag, String message) {
        logs.add(SieLog.builder().origin(entity.getClass()).level(SieLog.Level.WARNING).tag(tag).message(message).build());
    }

    protected void addCritical(String tag, String message) {
        logs.add(SieLog.builder().origin(entity.getClass()).level(SieLog.Level.CRITICAL).tag(tag).message(message).build());
    }
    
    protected void addCritical(Class c, SieException ex) {
        logs.add(SieLog.of(c, ex));
    }

    protected abstract void validate();

    protected Boolean isNullOrBlank(String input) {
        return input == null || input.isBlank();
    }

    private void init() {
        validate();
    }

    @Override
    public List<SieLog> getLogs() {
        return logs.stream().sorted().collect(Collectors.toList());
    }
}