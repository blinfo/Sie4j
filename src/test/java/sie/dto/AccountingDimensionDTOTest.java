package sie.dto;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class AccountingDimensionDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/accountingDimension.json");
            AccountingDimensionDTO result = new ObjectMapper().readValue(source, AccountingDimensionDTO.class);
            assertEquals(1, result.id().intValue());
            assertEquals("A", result.label());
            assertEquals(2, result.parentId().intValue());
        } catch (IOException ex) {
            fail();
        }
    }
}
