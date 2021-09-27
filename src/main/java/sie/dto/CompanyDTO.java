package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Company;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "corporateID", "aquisitionNumber", "sniCode", "type", "address", "id"})
public class CompanyDTO implements DTO {

    private final Company source;

    private CompanyDTO(Company company) {
        this.source = company;
    }

    public static CompanyDTO from(Company company) {
        return new CompanyDTO(company);
    }

    public String getName() {
        if (source.getName().isBlank()) {
            return null;
        }
        return source.getName();
    }

    public String getCorporateID() {
        return source.getCorporateID().map(s -> s.isBlank() ? null : s).orElse(null);
    }

    public Integer getAquisitionNumber() {
        return source.getAquisitionNumber().orElse(null);
    }

    public String getSniCode() {
        return source.getSniCode().map(s -> s.isBlank() ? null : s).orElse(null);
    }

    public TypeDTO getType() {
        return source.getType().map(TypeDTO::from).orElse(null);
    }

    public AddressDTO getAddress() {
        return source.getAddress().map(AddressDTO::from).orElse(null);
    }
    
    public String getId() {
        return source.getId().orElse(null);
    }

    @JsonPropertyOrder({"type", "description"})
    public static class TypeDTO implements DTO {

        private final Company.Type type;

        private TypeDTO(Company.Type type) {
            this.type = type;
        }

        public static TypeDTO from(Company.Type type) {
            return new TypeDTO(type);
        }

        public String getType() {
            return type.name();
        }

        public String getDescription() {
            return type.getDescription();
        }
    }
}
