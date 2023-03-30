package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class SieTypeDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/sieType.json");
            SieTypeDTO result = new ObjectMapper().readValue(source, SieTypeDTO.class);
            assertEquals("E4", result.type());
            assertEquals("Export av transaktioner", result.description());
        } catch (IOException ex) {
            fail();
        }
    }
}
