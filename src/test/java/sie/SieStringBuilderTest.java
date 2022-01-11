package sie;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.domain.Address;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.MetaData;
import sie.domain.Program;

/**
 *
 * @author Håkan Lidén
 */
public class SieStringBuilderTest {

    @Test
    public void test_address_with_empty_phone_and_missing_contact() {
        String addressLine = "#ADRESS \"\" \"Näsviksvägen 24\" \"820 64 Näsviken\" \"\"";
        Address address = Address.builder()
                .phone("")
                .streetAddress("Näsviksvägen 24")
                .postalAddress("820 64 Näsviken")
                .apply();
        Document document = createDocumentWithProvidedAddress(address);
        assertTrue(SieStringBuilder.parse(document).contains(addressLine));
    }

    @Test
    public void test_address_with_empty_postalAddress() {
        String addressLine = "#ADRESS \"TT\" \"Näsviksvägen 24\" \"\" \"+46 12 3456\"";
        Address address = Address.builder()
                .contact("TT")
                .phone("+46 12 3456")
                .streetAddress("Näsviksvägen 24")
                .apply();
        Document document = createDocumentWithProvidedAddress(address);
        assertTrue(SieStringBuilder.parse(document).contains(addressLine));
    }

    @Test
    public void test_address_with_incomplete_postalAddress() {
        String addressLine = "#ADRESS \"TT\" \"Näsviksvägen 24\" \"820 64\" \"+46 12 3456\"";
        Address address = Address.builder()
                .contact("TT")
                .phone("+46 12 3456")
                .streetAddress("Näsviksvägen 24")
                .postalAddress("820 64")
                .apply();
        Document document = createDocumentWithProvidedAddress(address);
        assertTrue(SieStringBuilder.parse(document).contains(addressLine));
    }

    @Test
    public void test_with_empty_address() {
        Document document = createDocumentWithProvidedAddress(null);
        assertFalse(SieStringBuilder.parse(document).contains("#ADRESS"));
    }

    private Document createDocumentWithProvidedAddress(Address address) {
        Company company = Company.builder("Övningsföretaget AB")
                .address(address)
                .apply();
        MetaData metaData = MetaData.builder()
                .sieType(Document.Type.E4)
                .company(company)
                .program(Program.of("Sie4j", "xxx"))
                .apply();
        return Document.builder()
                .metaData(metaData)
                .apply();
    }
}
