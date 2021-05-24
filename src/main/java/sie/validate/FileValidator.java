package sie.validate;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Håkan Lidén
 */
public class FileValidator implements Validator {

    private final List<SieError> errors;

    private FileValidator(List<SieError> errors) {
        this.errors = errors;
    }

    public static FileValidator from(List<SieError> errors) {
        return new FileValidator(errors);
    }

    public static FileValidator from(SieError error) {
        return new FileValidator(Arrays.asList(error));
    }

    @Override
    public List<SieError> getErrors() {
        return errors;
    }

}
