package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hl
 */
public class TransactionDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/transaction.json");
            TransactionDTO result = new ObjectMapper().readValue(source, TransactionDTO.class);
            assertEquals("1930", result.accountNumber());
            assertEquals(new BigDecimal("-1000.00"), result.amount());
            assertEquals("JT", result.signature());
            assertNull(result.text());
            assertEquals(LocalDate.of(2017, Month.JANUARY, 1), result.date());
            assertTrue(result.costCenterIds().isEmpty());
            assertFalse(result.costBearerIds().isEmpty());
            assertTrue(result.projectIds().isEmpty());
            assertEquals("TTT", result.costBearerIds().get(0));
            assertEquals(12.5, result.quantity(), 0D);
        } catch (IOException ex) {
            fail();
        }
    }

}
