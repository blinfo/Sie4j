package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;
import sie.dto.AccountDTO.ObjectIdDTO;

/**
 *
 * @author hl
 */
public class ObjectIdDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/objectId.json");
            ObjectIdDTO result = new ObjectMapper().readValue(source, ObjectIdDTO.class);
            assertEquals(2, result.dimensionId().intValue());
            assertEquals("TTT", result.objectNumber());
        } catch (IOException ex) {
            fail();
        }
    }
}
