package sie;

import java.util.Optional;
import static org.junit.Assert.*;
import org.junit.Test;
import sie.domain.*;
import sie.exception.*;
import sie.log.SieLog;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentFactoryTest {

    @Test
    public void test_file_with_missing_program_version() {
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/SIE_with_missing_program_version.se"));
        String message = "Programversion saknas";
        long size = 12l;
        assertEquals("List should contain " + size + " log", size, factory.getLogs().size());
        assertTrue("List should contain " + message, factory.getLogs().stream().map(l -> l.getMessage()).anyMatch(m -> m.equals(message)));
    }

    @Test
    public void test_file_with_erroneous_voucher_numbers() {
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_voucher_numbers.SI"));
        long size = 2l;
        String message = "Filer av typen I4 bör inte innehålla verifikationsnummer";
        assertEquals("List should contain " + size + " logs", size, factory.getLogs().size());
        assertEquals("Message should be " + message, message, factory.getLogs().get(0).getMessage());
        Optional<Voucher> optVoucher = factory.getDocument().getVouchers()
                .stream()
                .filter(voucher -> voucher.getNumber().isPresent()).findAny();
        assertFalse("Vouchers should not contain number", optVoucher.isPresent());
    }
    
    @Test
    public void file_with_long_voucher_series_numbers_should_add_info() {
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/SIE_with_long_voucher_series_number.SE"));
        String message = "Filen innehåller verifikationsserie vars nummer är längre än ett tecken";
        long count = factory.getDocument().getVouchers()
                .stream()
                .filter(voucher -> voucher.getSeries().orElse("").length() > 1)
                .count();
        assertEquals(6l, count);
        assertEquals(1, factory.getLogs().size());
        assertEquals(message, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_empty_voucher_date() {
        String expectedMessage = "Verifikationsdatum saknas";
        SieException ex = assertThrows("", MissingVoucherDateException.class, () -> DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_empty_voucher_date.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_missing_voucher_date() {
        String expectedMessage = "Verifikationsdatum saknas";
        SieException ex = assertThrows("", MissingVoucherDateException.class, () -> DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_voucher_date.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_critical_voucher_date_error() {
        String expectedMessage = "Kan inte läsa verifikationsdatum: 'rappakalja'\n"
                + " #VER \"K\" \"\" \"rappakalja\" \"Försäljning 25% (DF:157)\"  20180503";
        SieException ex = assertThrows("", InvalidVoucherDateException.class, () -> DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_critical_voucher_date_error.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_erroneous_voucher_date() {
        String expectedMessage = "Datum ska anges med åtta siffror - ååååmmdd - inte sex: '180501'";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_voucher_date.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_iso_voucher_date() {
        String expectedMessage = "Datum ska anges med åtta siffror - ååååmmdd utan bindestreck";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_iso_voucher_date.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_erroneous_transaction_should_throw_exception() {
        String expectedMessage = "Malformed line. '	#TRANS 1930'";
        InvalidTransactionDataException ex = assertThrows("", InvalidTransactionDataException.class, () -> DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_faulty_transaction.SI")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_erroneous_sru_line_should_contain_a_warning() {
        String expectedMessage = "Raden ska ha tre delar men tredje delen saknas: '#SRU 1110 '";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_missing_sru_code.SE"));
        Optional<SieLog> sieLog = factory.getWarnings().stream().filter(log -> log.getTag().isPresent() && log.getTag().get().equals("#" + Entity.SRU)).findFirst();
        assertTrue("Should contain a warning", sieLog.isPresent());
        assertEquals(expectedMessage, sieLog.get().getMessage());
    }

    @Test
    public void test_file_with_12_digit_cid() {
        String expectedMessage = "Organisationsnummer ska vara av formatet nnnnnn-nnnn. 165502261513";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_12_digit_cid.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_8_plus_4_digit_cid() {
        String expectedMessage = "Organisationsnummer ska vara av formatet nnnnnn-nnnn. 16550226-1513";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_8-4_digit_cid.SI"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_non_consecutive_years() {
        String expectedMessage = "Slutdatum för år -2 är inte direkt före nästa års startdatum";
        SieException ex = assertThrows("", NonConsecutiveFinancialYearsException.class, () -> DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE1_erroneous_leap_year.SE")));
        assertEquals("Message should be " + expectedMessage, expectedMessage, ex.getMessage());
    }

    @Test
    public void test_file_with_erroneous_taxation_year() {
        String expectedMessage = "Taxeringsår '2018 ÅRL' ska bara innehålla årtal";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_erroneous_taxar.SE"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    @Test
    public void test_file_with_unparseable_taxation_year() {
        String expectedMessage = "Taxeringsår tas bort då 'CCMXVIII' inte motsvarar ett numeriskt årtal";
        DocumentFactory factory = DocumentFactory.from(asByteArray("/sample/BLBLOV_SIE4_UTF_8_with_unparseable_taxar.SE"));
        assertEquals("Message should be " + expectedMessage, expectedMessage, factory.getLogs().get(0).getMessage());
    }

    private byte[] asByteArray(String path) {
        return SieReader.streamToByteArray(getClass().getResourceAsStream(path));
    }
}
