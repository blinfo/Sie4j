package sie.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.Helper;

/**
 *
 * @author Håkan Lidén
 *
 */
public class CompanyTest extends Helper {

    @Test
    public void test_Company_name() {
        Company company = getDocument(4, 'E').metaData().getCompany();
        String expectedResult = "Övningsföretaget AB";
        assertEquals(expectedResult, company.name());
    }

    @Test
    public void test_Company_id() {
        Company company = getDocument(4, 'E').metaData().getCompany();
        String expectedResult = "BLOV";
        assertTrue(company.optId().isPresent());
        assertEquals(expectedResult, company.optId().get());
    }

    @Test
    public void test_Company_CID() {
        Company company = getDocument(4, 'E').metaData().getCompany();
        String expectedResult = "165571-0918";
        assertTrue(company.optCorporateId().isPresent());
        assertEquals(expectedResult, company.optCorporateId().get());
    }

    @Test
    public void test_Company_Type() {
        Company company = getDocument(4, 'E').metaData().getCompany();
        Company.Type expectedResult = Company.Type.AB;
        assertTrue(company.optType().isPresent());
        assertEquals(expectedResult, company.optType().get());
    }

    //TODO: Add test for SNI-code (When write to SIE is implemented)
}
