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
public class FinancialYearDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/financialYear.json");
            FinancialYearDTO result = new ObjectMapper().readValue(source, FinancialYearDTO.class);
            assertEquals(0, result.index().intValue());
            assertEquals(LocalDate.of(2023, Month.JANUARY, 1), result.startDate());
            assertEquals(LocalDate.of(2023, Month.DECEMBER, 31), result.endDate());
        } catch (IOException ex) {
            fail();
        }
    }
}