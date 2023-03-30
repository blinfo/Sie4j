package sie.domain;

import java.util.*;

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
public final class Address implements Entity {

    private final String line;
    private final String contact;
    private final String streetAddress;
    private final String postalAddress;
    private final String phone;

    private Address(String line, String contact, String streetAddress, String postalAddress, String phone) {
        this.line = line;
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

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    /**
     * Getter for the contact.
     * <p>
     * Most commonly the name of the contact.
     *
     * @return String - the contact.
     */
    public String contact() {
        return Optional.ofNullable(contact).orElse("");
    }

    /**
     * Getter for the street address.
     *
     * @return String - the address.
     */
    public String streetAddress() {
        return Optional.ofNullable(streetAddress).orElse("");
    }

    /**
     * Getter for the postal address.
     * <p>
     * Usually zip code and city/location.
     *
     * @return String - the postal address
     */
    public String postalAddress() {
        return Optional.ofNullable(postalAddress).orElse("");
    }

    /**
     * Getter for the phone number.
     *
     * @return String - the phone number
     */
    public String phone() {
        return Optional.ofNullable(phone).orElse("");
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.contact);
        hash = 67 * hash + Objects.hashCode(this.streetAddress);
        hash = 67 * hash + Objects.hashCode(this.postalAddress);
        hash = 67 * hash + Objects.hashCode(this.phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Address other = (Address) obj;
        if (!Objects.equals(this.contact, other.contact)) {
            return false;
        }
        if (!Objects.equals(this.streetAddress, other.streetAddress)) {
            return false;
        }
        if (!Objects.equals(this.postalAddress, other.postalAddress)) {
            return false;
        }
        return Objects.equals(this.phone, other.phone);
    }

    public static class Builder {

        private String line;
        private String contact;
        private String streetAddress;
        private String postalAddress;
        private String phone;

        private Builder() {
        }
        
        public Builder line(String line) {
            this.line = line;
            return this;
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
            this.contact = contact == null ? null : contact.trim();
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
            this.streetAddress = streetAddress == null ? null : streetAddress.trim();
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
            this.postalAddress = postalAddress == null ? null : postalAddress.trim();
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
            this.phone = phone == null ? null : phone.trim();
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
            return new Address(line, contact, streetAddress, postalAddress, phone);
        }

    }
}
