package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Company;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "corporateID", "type"})
public class CompanyDTO implements DTO {

    private final Company source;

    private CompanyDTO(Company company) {
        this.source = company;
    }

    public static CompanyDTO from(Company company) {
        return new CompanyDTO(company);
    }

    public String getName() {
        return source.getName();
    }

    public String getCorporateID() {
        return source.getCorporateID().orElse(null);
    }

    public TypeDTO getType() {
        return source.getType().map(TypeDTO::from).orElse(null);
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
