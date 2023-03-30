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
public class PeriodicalBudgetDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/periodicalBudget.json");
            PeriodicalBudgetDTO result = new ObjectMapper().readValue(source, PeriodicalBudgetDTO.class);
            assertEquals(0, result.yearIndex().intValue());
            assertEquals(YearMonth.of(2022, Month.NOVEMBER), result.period());
            assertEquals(new BigDecimal("0.45"), result.amount());
        } catch (IOException ex) {
            fail();
        }
    }
}
