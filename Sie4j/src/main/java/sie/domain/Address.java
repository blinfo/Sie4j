package sie.domain;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class Address implements Entity {

    private final String contact;
    private final String streetAddress;
    private final String postalAddress;
    private final String phone;

    private Address(String contact, String streetAddress, String postalAddress, String phone) {
        this.contact = contact;
        this.streetAddress = streetAddress;
        this.postalAddress = postalAddress;
        this.phone = phone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getContact() {
        return contact;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Address{" 
                + "contact=" + contact + ", "
                + "streetAddress=" + streetAddress + ", "
                + "postalAddress=" + postalAddress + ", "
                + "phone=" + phone + '}';
    }

    public static class Builder {

        private String contact;
        private String streetAddress;
        private String postalAddress;
        private String phone;

        private Builder() {
        }

        public Builder contact(String contact) {
            this.contact = contact;
            return this;
        }

        public Builder streetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder postalAddress(String postalAddress) {
            this.postalAddress = postalAddress;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Address apply() {
            return new Address(contact, streetAddress, postalAddress, phone);
        }

    }
}
