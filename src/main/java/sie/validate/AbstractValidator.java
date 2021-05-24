package sie.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Document;
import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 * @param <T>
 */
abstract class AbstractValidator<T extends Entity> implements Validator {

    protected final T entity;
    protected final Document.Type type;
    private final List<SieError> errors;

    public AbstractValidator(T entity, Document.Type type) {
        this.entity = entity;
        this.type = type;
        errors = new ArrayList<>();
        init();
    }
    
    protected void addErrors(List<SieError> errors) {
        this.errors.addAll(errors);
    }

    protected void addInfo(String tag, String message) {
        errors.add(SieError.builder().origin(entity.getClass()).level(SieError.Level.INFO).tag(tag).message(message).build());
    }

    protected void addWarning(String tag, String message) {
        errors.add(SieError.builder().origin(entity.getClass()).level(SieError.Level.WARNING).tag(tag).message(message).build());
    }

    protected void addFatal(String tag, String message) {
        errors.add(SieError.builder().origin(entity.getClass()).level(SieError.Level.FATAL).tag(tag).message(message).build());
    }

    protected abstract void validate();

    protected Boolean isNullOrBlank(String input) {
        return input == null || input.trim().isEmpty();
    }
    
    private void init() {
        validate();
    }

    @Override
    public List<SieError> getErrors() {
        return errors.stream().sorted().collect(Collectors.toList());
    }

}
