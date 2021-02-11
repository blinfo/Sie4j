package sie.domain;

import java.util.Optional;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
public class Company implements Entity {

    private final String name;
    private final String id;
    private final Type type;
    private final String corporateId;
    private final String sniCode;
    private final Address address;

    private Company(String name, String id, Type type, String corporateID, String sniCode, Address address) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.corporateId = corporateID;
        this.sniCode = sniCode;
        this.address = address;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    public Optional<String> getCorporateID() {
        return Optional.ofNullable(corporateId);
    }

    public Optional<String> getSniCode() {
        return Optional.ofNullable(sniCode);
    }

    public Optional<Address> getAddress() {
        return Optional.ofNullable(address);
    }

    @Override
    public String toString() {
        return "Company{" 
                + "name=" + name + ", "
                + "id=" + id + ", "
                + "type=" + type + ", "
                + "corporateId=" + corporateId + ", "
                + "sniCode=" + sniCode + ", "
                + "address=" + address + '}';
    }

    public static class Builder {

        private String name;
        private String id;
        private Type type;
        private String corporateID;
        private String sniCode;
        private Address address;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder corporateID(String corporateID) {
            this.corporateID = corporateID;
            return this;
        }

        public Builder sniCode(String sniCode) {
            this.sniCode = sniCode;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Company apply() {
            return new Company(name, id, type, corporateID, sniCode, address);
        }
    }

    public enum Type {
        AB,
        BAB,
        BF,
        BFL,
        BRF,
        E,
        EK,
        FAB,
        FL,
        HB,
        I,
        KB,
        KHF,
        MB,
        OFB,
        S,
        SB,
        SCE,
        SE,
        SF,
        TSF,
        X;

        public static Type from(String string) {
            try {
                return valueOf(string.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException ex) {
                return X;
            }
        }

    }
}
