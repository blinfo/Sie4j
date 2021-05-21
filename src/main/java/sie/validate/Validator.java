package sie.validate;

import java.util.List;
import java.util.stream.Collectors;
import sie.validate.model.SieError;

/**
 *
 * @author Håkan Lidén
 */
public interface Validator {

    List<SieError> getErrors();

    default List<SieError> getFatalErrors() {
        return getErrors().stream().filter(error -> error.getLevel().equals(SieError.Level.FATAL)).collect(Collectors.toList());
    }

    default List<SieError> getWarnings() {
        return getErrors().stream().filter(error -> error.getLevel().equals(SieError.Level.WARNING)).collect(Collectors.toList());
    }
}
