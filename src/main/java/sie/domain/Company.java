package sie.domain;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Company implements Entity {

    private final String name;
    private final String id;
    private final Type type;
    private final String corporateId;
    private final Integer aquisitionNumber;
    private final String sniCode;
    private final Address address;

    private Company(String name, String id, Type type, String corporateID, Integer aquisitionNumber, String sniCode, Address address) {
        this.name = Objects.requireNonNull(name);
        this.id = id;
        this.type = type;
        this.corporateId = corporateID;
        this.aquisitionNumber = aquisitionNumber;
        this.sniCode = sniCode;
        this.address = address;
    }

    public static Builder builder(String name) {
        return new Builder(name);
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
        if (corporateId == null || corporateId.trim().isEmpty()) {
            return Optional.empty();
        }
        if (corporateId.matches("\\d{6}-\\d{4}")) {
            return Optional.of(corporateId);
        }
        return Optional.of(corporateId).filter(cid -> cid.matches("\\d{10}")).map(cid -> cid.substring(0, 6) + "-" + cid.substring(6));
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
                + "corporateId=" + getCorporateID().orElse("") + ", "
                + "aquisitionNumber=" + aquisitionNumber + ", "
                + "sniCode=" + sniCode + ", "
                + "address=" + address + '}';
    }

    public static class Builder {

        private final String name;
        private String id;
        private Type type;
        private String corporateID;
        private Integer aquisitionNumber;
        private String sniCode;
        private Address address;

        private Builder(String name) {
            this.name = name;
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

        public Builder aquisitionNumber(Integer aquisitionNumber) {
            this.aquisitionNumber = aquisitionNumber;
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
            return new Company(name, id, type, corporateID, aquisitionNumber, sniCode, address);
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
