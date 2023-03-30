package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Company;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "corporateID", "aquisitionNumber", "sniCode", "type", "address", "id"})
public record CompanyDTO(String name,
        String corporateId,
        Integer aquisitionNumber,
        String sniCode,
        TypeDTO type,
        AddressDTO address,
        String id) implements DTO {

    public static CompanyDTO from(Company company) {
        return new CompanyDTO(
                company.getName(),
                company.getCorporateID().orElse(null),
                company.getAquisitionNumber().orElse(null),
                company.getSniCode().orElse(null),
                company.getType().map(TypeDTO::from).orElse(null),
                company.getAddress().map(AddressDTO::from).orElse(null),
                company.getId().orElse(null));
    }

    @JsonPropertyOrder({"type", "description"})
    public static record TypeDTO(String type, String description) implements DTO {

        public static TypeDTO from(Company.Type type) {
            return new TypeDTO(type.name(), type.getDescription());
        }
    }
}
