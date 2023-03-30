package sie.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hl
 */
public class AccountingPlanDTOTest {

    @Test
    public void test_deserialize() {
        try {
            InputStream source = getClass().getResourceAsStream("/source/accountingPlan.json");
            AccountingPlanDTO result = new ObjectMapper().readValue(source, AccountingPlanDTO.class);
            assertEquals("BAS97", result.type());
            assertFalse(result.accounts().isEmpty());
            AccountDTO account = result.accounts().get(0);
            assertEquals("1110", account.number());
            assertEquals("Byggnader", account.label());
            assertEquals(1, account.sruCodes().size());
            assertEquals("7214", account.sruCodes().get(0));
            assertEquals(1, account.openingBalances().size());
            assertEquals(-1, account.openingBalances().get(0).yearIndex().intValue());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }
}
