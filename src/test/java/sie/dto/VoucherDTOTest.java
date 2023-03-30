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
public class VoucherDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/voucher.json");
            VoucherDTO result = new ObjectMapper().readValue(source, VoucherDTO.class);
            assertEquals("A", result.series());
            assertEquals(1, result.number().intValue());
            assertEquals(LocalDate.of(2017, Month.JANUARY, 1), result.date());
            assertEquals(LocalDate.of(2017, Month.JANUARY, 19), result.registrationDate());
            assertEquals("Representation måltid utan alkohol", result.text());
            assertEquals("#6 Linda Henriksson", result.signature());
            assertTrue(result.balanced());
            assertNull(result.diff());
            assertFalse(result.transactions().isEmpty());
            TransactionDTO trans = result.transactions().get(0);
            assertEquals("1930", trans.accountNumber());
            assertEquals(new BigDecimal("-1000.00"), trans.amount());
            assertNull(trans.signature());
        } catch (IOException ex) {
            fail();
        }
    }

}
