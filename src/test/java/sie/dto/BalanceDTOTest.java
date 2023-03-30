package sie.dto;

import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hl
 */
public class BalanceDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/balance.json");
            BalanceDTO result = new ObjectMapper().readValue(source, BalanceDTO.class);
            assertEquals(-1, result.yearIndex().intValue());
            assertEquals(new BigDecimal("121.50"), result.amount());
        } catch (IOException ex) {
            fail();
        }
    }
}
