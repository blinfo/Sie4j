package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hl
 */
public class GeneratedDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/generated.json");
            GeneratedDTO result = new ObjectMapper().readValue(source, GeneratedDTO.class);
            assertEquals("JT", result.signature());
            assertEquals(LocalDate.of(2023, Month.MARCH, 30), result.date());
        } catch (IOException ex) {
            fail();
        }
    }
}
