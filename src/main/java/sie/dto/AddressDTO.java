package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Address;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"contact", "streetAddress", "postalAddress", "phone"})
public class AddressDTO {

    private final Address source;

    private AddressDTO(Address source) {
        this.source = source;
    }

    public static AddressDTO from(Address source) {
        return new AddressDTO(source);
    }

    public String getContact() {
        if (source.getContact().isBlank()) {
            return null;
        }
        return source.getContact();
    }

    public String getPhone() {
        if (source.getPhone().isBlank()) {
            return null;
        }
        return source.getPhone();
    }

    public String getStreetAddress() {
        if (source.getStreetAddress().isBlank()) {
            return null;
        }
        return source.getStreetAddress();
    }

    public String getPostalAddress() {
        if (source.getPostalAddress().isBlank()) {
            return null;
        }
        return source.getPostalAddress();
    }
}
