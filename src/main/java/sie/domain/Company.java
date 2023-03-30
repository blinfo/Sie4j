package sie.domain;

import java.util.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class Company implements Entity {

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

    @Override
    public Optional<String> optLine() {
        return Optional.empty();
    }

    public String name() {
        return name;
    }

    public Optional<String> optId() {
        return Optional.ofNullable(id);
    }

    public Optional<Type> optType() {
        return Optional.ofNullable(type);
    }

    public Optional<String> optCorporateId() {
        return Optional.ofNullable(corporateId);
    }

    public Optional<Integer> optAquisitionNumber() {
        return Optional.ofNullable(aquisitionNumber);
    }
    
    public Optional<String> optSniCode() {
        return Optional.ofNullable(sniCode);
    }

    public Optional<Address> optAddress() {
        return Optional.ofNullable(address);
    }

    @Override
    public String toString() {
        return "Company{"
                + "name=" + name + ", "
                + "id=" + id + ", "
                + "type=" + type + ", "
                + "corporateId=" + optCorporateId().orElse("") + ", "
                + "aquisitionNumber=" + aquisitionNumber + ", "
                + "sniCode=" + sniCode + ", "
                + "address=" + address + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.type);
        hash = 37 * hash + Objects.hashCode(this.corporateId);
        hash = 37 * hash + Objects.hashCode(this.aquisitionNumber);
        hash = 37 * hash + Objects.hashCode(this.sniCode);
        hash = 37 * hash + Objects.hashCode(this.address);
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
        final Company other = (Company) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.corporateId, other.corporateId)) {
            return false;
        }
        if (!Objects.equals(this.sniCode, other.sniCode)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.aquisitionNumber, other.aquisitionNumber)) {
            return false;
        }
        return Objects.equals(this.address, other.address);
    }

    public static class Builder {

        private final String name;
        private String id;
        private Type type;
        private String corporateId;
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

        public Builder corporateId(String corporateId) {
            this.corporateId = corporateId;
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
            return new Company(name, id, type, corporateId, aquisitionNumber, sniCode, address);
        }
    }

    public enum Type {
        AB("Aktiebolag"),
        BAB("Bankaktiebolag"),
        BF("Bostadsförening"),
        BFL("Utländsk banks filial"),
        BRF("Bostadsrättsförening"),
        E("Enskild näringsidkare"),
        EK("Ekonomisk förening"),
        FAB("Försäkringsaktiebolag"),
        FL("Filial till utländskt bolag"),
        HB("Handelsbolag"),
        I("Ideell förening som bedriver näring"),
        KB("Kommanditbolag"),
        KHF("Kooperativ hyresrättsförening"),
        MB("Medlemsbank"),
        OFB("Ömsesidigt försäkringsbolag"),
        SB("Sparbank"),
        SCE("Europakooperativ"),
        SE("Europabolag"),
        SF("Sambruksförening"),
        S("Stiftelse som bedriver näring"),
        TSF("Trossamfund"),
        X("Annan företagsform");

        private final String description;

        private Type(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static Type from(String string) {
            try {
                return valueOf(string.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException ex) {
                return X;
            }
        }
    }
}
