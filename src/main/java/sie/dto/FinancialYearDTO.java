package sie.dto;

import sie.domain.FinancialYear;

/**
 *
 * @author Håkan Lidén
 */
public class FinancialYearDTO implements DTO {

    private final FinancialYear source;

    private FinancialYearDTO(FinancialYear source) {
        this.source = source;
    }

    public static FinancialYearDTO from(FinancialYear source) {
        return new FinancialYearDTO(source);
    }

    public Integer getIndex() {
        return source.getIndex();
    }

    public String getStartDate() {
        return source.getStartDate().toString();
    }

    public String getEndDate() {
        return source.getEndDate().toString();
    }
}
