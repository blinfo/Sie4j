package sie.domain;

/**
 * Address - The address of the Company present in the document.
 * <p>
 * Note that if an address is present, all four fields are supposed to be
 * populated. The standard does not specify how the fields are to be used, for
 * example whether the name should be "first_name last_name" or "last_name,
 * first_name". The most common usage is probably as provided in the example
 * below:
 * <p>
 * SIE:
 * <code>#ADRESS "Vigdis Grönvall" "Björkbranten 123" "834 31 Brunflo" "072-29441879"</code>
 * <p>
 * The address in the sample is not a valid address or to an existing person.
 * Also, the phone number isn't a valid Swedish number, containing the wrong
 * number of digits.
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

    /**
     * Static instantiation of the Address.Builder.
     *
     * @return Address.Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for the contact.
     * <p>
     * Most commonly the name of the contact.
     *
     * @return String - the contact.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Getter for the street address.
     *
     * @return String - the address.
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Getter for the postal address.
     * <p>
     * Usually zip code and city/location.
     *
     * @return String - the postal address
     */
    public String getPostalAddress() {
        return postalAddress;
    }

    /**
     * Getter for the phone number.
     *
     * @return String - the phone number
     */
    public String getPhone() {
        return phone;
    }

    public Boolean isEmpty() {
        return (contact == null || contact.isEmpty())
                && (streetAddress == null || streetAddress.isEmpty())
                && (postalAddress == null || postalAddress.isEmpty())
                && (phone == null || phone.isEmpty());
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

        /**
         * Required - Contact.
         * <p>
         * Name of the contact. Could possibly also be used for email address.
         * <p>
         * SIE: <code>#ADRESS <b>"Vigdis Grönvall"</b> "Björkbranten 123" "834
         * 31 Brunflo" "072-29441879"</code>
         *
         * @param contact String
         * @return Address.Builder
         */
        public Builder contact(String contact) {
            this.contact = contact;
            return this;
        }

        /**
         * Required - Street Address.
         * <p>
         * SIE: <code>#ADRESS "Vigdis Grönvall" <b>"Björkbranten 123"</b> "834
         * 31 Brunflo" "072-29441879"</code>
         *
         * @param streetAddress String
         * @return Address.Builder
         */
        public Builder streetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        /**
         * Required - Postal Address.
         * <p>
         * Preferred usage is zip code followed by city/location.
         * <p>
         * SIE: <code>#ADRESS "Vigdis Grönvall" "Björkbranten 123" <b>"834 31
         * Brunflo"</b> "072-29441879"</code>
         *
         * @param postalAddress String
         * @return Address.Builder
         */
        public Builder postalAddress(String postalAddress) {
            this.postalAddress = postalAddress;
            return this;
        }

        /**
         * Required - Phone Number.
         * <p>
         * The phone number, with or without country code (which should be
         * presumed to be the Swedish country code, +46 if missing).
         * <p>
         * SIE:
         * <code>#ADRESS "Vigdis Grönvall" "Björkbranten 123" "834 31 Brunflo" <b>"072-29441879"</b></code>
         *
         * @param phone String
         * @return Address.Builder
         */
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Boolean isEmpty() {
            return (contact == null || contact.isBlank())
                    && (streetAddress == null || streetAddress.isBlank())
                    && (postalAddress == null || postalAddress.isBlank())
                    && (phone == null || phone.isBlank());
        }

        /**
         *
         * @return Address representing the data in the builder.
         */
        public Address apply() {
            return new Address(contact, streetAddress, postalAddress, phone);
        }

    }
}
