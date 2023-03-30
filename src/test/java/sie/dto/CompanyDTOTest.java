package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class CompanyDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/company.json");
            CompanyDTO result = new ObjectMapper().readValue(source, CompanyDTO.class);
            assertEquals("Övningsföretaget AB", result.name());
            assertEquals("BLOV", result.id());
            assertEquals(1, result.aquisitionNumber().intValue());
            assertEquals("91-34", result.sniCode());
            assertNotNull(result.type());
            assertEquals("AB", result.type().type());
            assertEquals("Aktiebolag", result.type().description());
            assertNotNull(result.address());
            assertEquals("Jaques Tati", result.address().contact());
            assertEquals("165571-0918", result.corporateId());
        } catch (IOException ex) {
            fail();
        }
    }
}
