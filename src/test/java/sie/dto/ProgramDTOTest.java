package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class ProgramDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/program.json");
            ProgramDTO result = new ObjectMapper().readValue(source, ProgramDTO.class);
            assertEquals("Qwerty Bokföring", result.name());
            assertEquals("2024.001.1", result.version());
        } catch (IOException ex) {
            fail();
        }
    }
}
