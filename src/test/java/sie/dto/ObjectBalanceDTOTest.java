package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.math.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class ObjectBalanceDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/objectBalance.json");
            ObjectBalanceDTO result = new ObjectMapper().readValue(source, ObjectBalanceDTO.class);
            assertEquals(new BigDecimal("20154.00"), result.amount());
            assertEquals(0, result.yearIndex().intValue());
            assertEquals(1d, result.quantity(), 0d);
            assertNotNull(result.objectId());
            assertEquals(2, result.objectId().dimensionId().intValue());
        } catch (IOException ex) {
            fail();
        }
    }
}
