package sie.domain;

import static org.junit.Assert.*;
import org.junit.Test;
import sie.Helper;

/**
 *
 * @author Håkan Lidén
 *
 */
public class CompanyTest extends Helper {

    @Test
    public void test_Company_name() {
        Company company = getDocument(4, 'E').getMetaData().getCompany();
        String expectedResult = "Övningsföretaget AB";
        assertEquals("Company name should be " + expectedResult, expectedResult, company.getName());
    }

    @Test
    public void test_Company_id() {
        Company company = getDocument(4, 'E').getMetaData().getCompany();
        String expectedResult = "BLOV";
        assertTrue("Company should have id", company.getId().isPresent());
        assertEquals("Company id should be " + expectedResult, expectedResult, company.getId().get());
    }

    @Test
    public void test_Company_CID() {
        Company company = getDocument(4, 'E').getMetaData().getCompany();
        String expectedResult = "165571-0918";
        assertTrue("Company should have cid", company.getCorporateID().isPresent());
        assertEquals("Company cid should be " + expectedResult, expectedResult, company.getCorporateID().get());
    }

    @Test
    public void test_Company_Type() {
        Company company = getDocument(4, 'E').getMetaData().getCompany();
        Company.Type expectedResult = Company.Type.AB;
        assertTrue("Company should have type", company.getType().isPresent());
        assertEquals("Company type should be " + expectedResult, expectedResult, company.getType().get());
    }

    //TODO: Add test for SNI-code (When write to SIE is implemented)
}
