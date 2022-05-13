package sie.domain;

import java.time.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;
import sie.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public class MetaDataTest extends Helper {

    @Test
    public void test_Document_isNotRead() {
        Boolean isRead = getDocument(4, 'E').getMetaData().isRead();
        assertFalse("Document should not be read", isRead);
    }

    @Test
    public void test_Document_isRead() {
        Document doc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE1_file_is_read.SE"));
        assertTrue("Document should be signaled as read", doc.getMetaData().isRead());
    }

    @Test
    public void test_Document_format() {
        Document.Type sieType = getDocument(4, 'E').getMetaData().getSieType();
        Document.Type expectedResult = Document.Type.E4;
        assertEquals("Document should be type " + expectedResult, expectedResult, sieType);
    }

    @Test
    public void test_Document_taxationYear() {
        Year expectedResult = Year.of(2018);
        Optional<Year> optYear = getDocument(4, 'E').getMetaData().getTaxationYear();
        assertTrue("Taxation year should exist", optYear.isPresent());
        assertEquals("Taxation year should be " + expectedResult, expectedResult, optYear.get());
    }

    @Test
    public void test_Document_financialYears() {
        List<FinancialYear> years = getDocument(4, 'E').getMetaData().getFinancialYears();
        Integer expectedNoOfYears = 2;
        Integer expectedIndex = -1;
        LocalDate expectedStartDate = LocalDate.parse("2017-01-01");
        assertEquals("Document should contain two years", expectedNoOfYears, Integer.valueOf(years.size()));
        assertEquals("First year should have start date ", expectedStartDate, years.get(0).getStartDate());
        assertTrue("Second year should end the day before the first year starts ", years.get(1).getEndDate().plusDays(1).equals(years.get(0).getStartDate()));
        assertEquals("Second year should have index " + expectedIndex, expectedIndex, years.get(1).getIndex());
    }
}
