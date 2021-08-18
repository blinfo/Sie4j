package sie;

import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
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
        long size = 2l;
        assertEquals("List should contain " + size + " log", size, factory.getLogs().size());
        assertEquals("Message should be " + message, message, factory.getWarnings().get(0).getMessage());
    }

    @Test
    public void test_file_with_erroneous_voucher_numbers() {
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_voucher_numbers.SI"));
        long size = 1l;
        String message = "Filer av typen I4 bör inte innehålla verifikationsnummer";
        assertEquals("List should contain " + size + " logs", size, factory.getLogs().size());
        assertEquals("Message should be " + message, message, factory.getLogs().get(0).getMessage());
        Optional<Voucher> optVoucher = factory.getDocument().getVouchers()
                .stream()
                .filter(voucher -> voucher.getNumber().isPresent()).findAny();
        assertFalse("Vouchers should not contain number", optVoucher.isPresent());
    }

    @Test
    public void test_file_with_empty_voucher_date() {
        String expectedMessage = "Verifikationsdatum är tomt";
        SieException ex = assertThrows("", SieException.class, () -> DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_empty_voucher_date.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_missing_voucher_date() {
        String expectedMessage = "Verifikationsdatum saknas";
        SieException ex = assertThrows("", SieException.class, () -> DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_missing_voucher_date.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_critical_voucher_date_error() {
        String expectedMessage = "Kan inte läsa verifikationsdatum: \"rappakalja\"";
        SieException ex = assertThrows("", SieException.class, () -> DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_critical_voucher_date_error.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_erroneous_voucher_date() {
        String expectedMessage = "Datum ska anges med åtta siffror - ååååmmdd - inte sex: \"180501\"";
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_voucher_date.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_iso_voucher_date() {
        String expectedMessage = "Datum ska anges med åtta siffror - ååååmmdd utan bindestreck";
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_iso_voucher_date.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_12_digit_cid() {
        String expectedMessage = "Organisationsnummer ska vara av formatet nnnnnn-nnnn";
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_12_digit_cid.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_8_plus_4_digit_cid() {
        String expectedMessage = "Organisationsnummer ska vara av formatet nnnnnn-nnnn";
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_8-4_digit_cid.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_non_consecutive_years() {
        String expectedMessage = "Slutdatum för år -2 är inte direkt före nästa års startdatum";
        SieException ex = assertThrows("", SieException.class, () -> DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE1_erroneous_leap_year.SE")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_erroneous_taxation_year() {
        String expectedMessage = "Taxeringsår \"2018 ÅRL\" ska bara innehålla årtal";
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_taxar.SE"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_unparseable_taxation_year() {
        String expectedMessage = "Taxeringsår tas bort då \"CCMXVIII\" inte motsvarar ett årtal";
        DocumentFactory factory = DocumentFactory.from(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8_with_unparseable_taxar.SE"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }
}
