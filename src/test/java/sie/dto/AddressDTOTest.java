package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hl
 */
public class AddressDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/address.json");
            AddressDTO result = new ObjectMapper().readValue(source, AddressDTO.class);
            assertEquals("Leverans", result.line());
            assertEquals("Jaques Tati", result.contact());
            assertEquals("Box 84", result.streetAddress());
            assertEquals("123 21 Nånstad", result.postalAddress());
            assertEquals("+00-000000", result.phone());
        } catch (IOException ex) {
            fail();
        }
    }
}
