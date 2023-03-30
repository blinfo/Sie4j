package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hl
 */
public class PeriodicalBalanceDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/periodicalBalance.json");
            PeriodicalBalanceDTO result = new ObjectMapper().readValue(source, PeriodicalBalanceDTO.class);
            assertEquals(0, result.yearIndex().intValue());
            assertEquals(YearMonth.of(2023, Month.JANUARY), result.period());
            assertNotNull(result.objectId());
            assertEquals("TTT", result.objectId().objectNumber());
            assertEquals(new BigDecimal("1097.50"), result.amount());
            assertEquals(13.54, result.quantity(), 0D);
        } catch (IOException ex) {
            fail();
        }
    }
}
