package sie.validate;

import java.util.List;
import org.junit.Test;
import sie.domain.Document;
import sie.validate.model.SieError;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidatorTest extends AbstractValidatorTest{

    @Test
    public void test_file_with_errors() {
        Document document = getDocument("BLBLOV_SIE4_UTF_8_with_errors.SE");
        List<SieError> errors = DocumentValidator.of(document).getErrors();
        errors.forEach(System.out::println);
    }
}
