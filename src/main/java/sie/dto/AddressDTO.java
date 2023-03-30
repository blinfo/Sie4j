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
        String line = source.optLine().orElse(null);
        String contact = source.contact() == null || source.contact().isBlank() ? null : source.contact();
        String streetAddress = source.streetAddress() == null || source.streetAddress().isBlank() ? null : source.streetAddress();
        String postalAddress = source.postalAddress() == null || source.postalAddress().isBlank() ? null : source.postalAddress();
        String phone = source.phone() == null || source.phone().isBlank() ? null : source.phone();
        return new AddressDTO(line, contact, streetAddress, postalAddress, phone);
    }
}
