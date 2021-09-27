package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Company;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "corporateID", "aquisitionNumber", "sniCode", "type", "address", "id"})
public class CompanyDTO implements DTO {

    private String name;
    private String corporateId;
    private Integer aquisitionNumber;
    private String sniCode;
    private TypeDTO type;
    private AddressDTO address;
    private String id;

    public static CompanyDTO from(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setName(company.getName());
        company.getCorporateID().ifPresent(dto::setCorporateId);
        company.getAquisitionNumber().ifPresent(dto::setAquisitionNumber);
        company.getSniCode().ifPresent(dto::setSniCode);
        company.getType().map(TypeDTO::from).ifPresent(dto::setType);
        company.getAddress().map(AddressDTO::from).ifPresent(dto::setAddress);
        company.getId().ifPresent(dto::setId);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public Integer getAquisitionNumber() {
        return aquisitionNumber;
    }

    public void setAquisitionNumber(Integer aquisitionNumber) {
        this.aquisitionNumber = aquisitionNumber;
    }

    public String getSniCode() {
        return sniCode;
    }

    public void setSniCode(String sniCode) {
        this.sniCode = sniCode;
    }

    public TypeDTO getType() {
        return type;
    }

    public void setType(TypeDTO type) {
        this.type = type;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonPropertyOrder({"type", "description"})
    public static class TypeDTO implements DTO {

        private String type;
        private String description;

        public TypeDTO() {
        }

        private TypeDTO(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public static TypeDTO from(Company.Type type) {
            return new TypeDTO(type.name(), type.getDescription());
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}
