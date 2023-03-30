package sie.domain;

import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public class MetaDataTest extends Helper {

    @Test
    public void test_Document_isNotRead() {
        Boolean isRead = getDocument(4, 'E').metaData().isRead();
        assertFalse(isRead);
    }

    @Test
    public void test_Document_isRead() {
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE1_file_is_read.SE"));
        assertTrue(doc.metaData().isRead());
    }

    @Test
    public void test_Document_format() {
        Document.Type sieType = getDocument(4, 'E').metaData().sieType();
        Document.Type expectedResult = Document.Type.E4;
        assertEquals(expectedResult, sieType);
    }

    @Test
    public void test_Document_taxationYear() {
        Year expectedResult = Year.of(2018);
        Optional<Year> optYear = getDocument(4, 'E').metaData().optTaxationYear();
        assertTrue(optYear.isPresent());
        assertEquals(expectedResult, optYear.get());
    }

    @Test
    public void test_Document_financialYears() {
        List<FinancialYear> years = getDocument(4, 'E').metaData().financialYears();
        Integer expectedNoOfYears = 2;
        Integer expectedIndex = -1;
        LocalDate expectedStartDate = LocalDate.parse("2017-01-01");
        assertEquals(expectedNoOfYears, Integer.valueOf(years.size()));
        assertEquals(expectedStartDate, years.get(0).startDate());
        assertTrue(years.get(1).endDate().plusDays(1).equals(years.get(0).startDate()));
        assertEquals(expectedIndex, years.get(1).index());
    }
}
