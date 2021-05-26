package sie;

import java.util.List;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentFactoryTest {

    @Test
    public void test_file_with_missing_program_version() {
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/SIE_with_missing_program_version.se"));
        String message = "Programversion saknas";
        long size = 4l;
        factory.getLogs().forEach(System.out::println);
        assertEquals("List should contain " + size + " log", size, factory.getLogs().size());
        assertEquals("Message should be " + message, message, factory.getWarnings().get(0).getMessage());
    }

    @Test
    public void test_file_with_erroneous_voucher_numbers() {
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_voucher_numbers.SI"));
        long size = 2l;
        String message = "Filer av typen I4 bör inte innehålla verifikationsnummer";
        assertEquals("List should contain " + size + " logs", size, factory.getLogs().size());
        assertEquals("Message should be " + message, message, factory.getLogs().get(0).getMessage());
        Optional<Voucher> optVoucher = factory.getDocument().getVouchers()
                .stream()
                .filter(voucher -> voucher.getNumber().isPresent()).findAny();
        assertFalse("Vouchers should not contain number", optVoucher.isPresent());
    }
}
