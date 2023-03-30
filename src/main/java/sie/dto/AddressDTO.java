package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Address;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"contact", "streetAddress", "postalAddress", "phone"})
public record AddressDTO(String line, String contact, String streetAddress, String postalAddress, String phone) implements DTO {

    public static AddressDTO from(Address source) {
        String line = source.getLine().orElse(null);
        String contact = source.getContact() == null || source.getContact().isBlank() ? null : source.getContact();
        String streetAddress = source.getStreetAddress() == null || source.getStreetAddress().isBlank() ? null : source.getStreetAddress();
        String postalAddress = source.getPostalAddress() == null || source.getPostalAddress().isBlank() ? null : source.getPostalAddress();
        String phone = source.getPhone() == null || source.getPhone().isBlank() ? null : source.getPhone();
        return new AddressDTO(line, contact, streetAddress, postalAddress, phone);
    }
}
