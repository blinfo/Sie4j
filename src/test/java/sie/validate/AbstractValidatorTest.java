package sie.validate;

import sie.Sie4j;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public abstract class AbstractValidatorTest {

    protected Document getDocument(String filename) {
        return Sie4j.fromSie(getClass().getResourceAsStream("/sample/" + filename));
    }
}
