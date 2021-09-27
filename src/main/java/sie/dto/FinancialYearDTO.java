package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.FinancialYear;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"index", "startDate", "endDate"})
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
