package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class AccountingObjectDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/accountingObject.json");
            AccountingObjectDTO result = new ObjectMapper().readValue(source, AccountingObjectDTO.class);
            assertEquals(1, result.dimensionId().intValue());
            assertEquals("A", result.number());
            assertEquals("APA232", result.label());
        } catch (IOException ex) {
            fail();
        }
    }
}
