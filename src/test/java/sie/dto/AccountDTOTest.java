package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class AccountDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/account.json");
            AccountDTO result = new ObjectMapper().readValue(source, AccountDTO.class);
            assertEquals("1110", result.number());
            assertEquals("Byggnader", result.label());
            assertEquals(1, result.sruCodes().size());
            assertEquals("7214", result.sruCodes().get(0));
            assertEquals(1, result.openingBalances().size());
            assertEquals(-1, result.openingBalances().get(0).yearIndex().intValue());
        } catch (IOException ex) {
            fail();
        }
    }
}
