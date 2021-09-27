package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import sie.domain.FinancialYear;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"index", "startDate", "endDate"})
public class FinancialYearDTO implements DTO {

    private Integer index;
    private String startDate;
    private String endDate;

    public FinancialYearDTO() {
    }

    private FinancialYearDTO(Integer index, LocalDate startDate, LocalDate endDate) {
        this.index = index;
        this.startDate = startDate.toString();
        this.endDate = endDate.toString();
    }

    public static FinancialYearDTO from(FinancialYear source) {
        return new FinancialYearDTO(source.getIndex(), source.getStartDate(), source.getEndDate());
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
