package sie.dto;

import sie.domain.Company;

/**
 *
 * @author Håkan Lidén
 */
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

    public String getType() {
        return source.getType().map(Company.Type::name).orElse(null);
    }
}
