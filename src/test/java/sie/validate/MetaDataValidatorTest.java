package sie.validate;

import java.util.Arrays;
import org.junit.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class MetaDataValidatorTest extends AbstractValidatorTest {

    @Test
    public void test_metaData() {
        Arrays.asList("CC3.SI", "Transaktioner per Z-rapport.se", "SIE_with_missing_program_version.se", "BLBLOV_SIE3.SE").forEach(file -> {
            System.out.println("File: " + file);
            Document doc = getDocument(file);
            MetaDataValidator.from(doc.getMetaData()).getLogs().forEach(System.out::println);
            System.out.println("");
        });
    }

}
