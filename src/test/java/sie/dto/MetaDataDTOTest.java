package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.time.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class MetaDataDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/metaData.json");
            MetaDataDTO result = new ObjectMapper().readValue(source, MetaDataDTO.class);
            assertNotNull(result.company());
            assertEquals("Övningsföretaget AB", result.company().name());
            assertNotNull(result.company().type());
            assertEquals("AB", result.company().type().type());
            assertNotNull(result.program());
            assertEquals("BL Administration", result.program().name());
            assertEquals("2018.2.101", result.program().version());
            assertNotNull(result.generated());
            assertNotNull(result.sieType());
            assertNull(result.comments());
            assertEquals(Year.of(2018), result.taxationYear());
            assertEquals(LocalDate.of(2020, Month.FEBRUARY, 28), result.periodRange());
            assertEquals("SEK", result.currency());
            assertFalse(result.read());
        } catch (IOException ex) {
            fail();
        }
    }

}
