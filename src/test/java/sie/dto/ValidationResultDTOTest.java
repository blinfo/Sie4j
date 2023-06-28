package sie.dto;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author hl
 */
public class ValidationResultDTOTest {

    @Test
    public void test_addLog() {
        List<SieLogDTO> logs = List.of(SieLogDTO.of("info", "Testmeddelande 1", null, null));
        ValidationResultDTO dto = ValidationResultDTO.from(null, logs);
        SieLogDTO log2 = SieLogDTO.of("info", "Testmeddelande 2", null, null);
        ValidationResultDTO dto2 = dto.addLog(log2);
        assertNotEquals(dto, dto2);
        dto = dto.addLog(log2);
        assertEquals(dto, dto2);
    }
}
