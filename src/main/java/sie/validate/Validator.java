package sie.validate;

import java.util.List;
import java.util.stream.Collectors;

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
    
    default List<SieError> getInfo() {
        return getErrors().stream().filter(error -> error.getLevel().equals(SieError.Level.INFO)).collect(Collectors.toList());
    }
}
