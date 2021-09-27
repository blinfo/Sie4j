package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Address;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"contact", "streetAddress", "postalAddress", "phone"})
public class AddressDTO {

    private String contact;
    private String streetAddress;
    private String postalAddress;
    private String phone;

    public AddressDTO() {
    }

    public static AddressDTO from(Address source) {
        AddressDTO dto = new AddressDTO();
        dto.setContact(source.getContact().isBlank() ? null : source.getContact());
        dto.setStreetAddress(source.getStreetAddress().isBlank() ? null : source.getStreetAddress());
        dto.setPostalAddress(source.getPostalAddress().isBlank() ? null : source.getPostalAddress());
        dto.setPhone(source.getPhone().isBlank() ? null : source.getPhone());
        return dto;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
