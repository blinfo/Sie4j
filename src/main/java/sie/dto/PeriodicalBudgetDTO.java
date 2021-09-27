package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.PeriodicalBudget;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "period", "amount"})
public class PeriodicalBudgetDTO implements DTO {

    private Integer yearIndex;
    private String period;
    private BigDecimal amount;

    public static PeriodicalBudgetDTO from(PeriodicalBudget source) {
        PeriodicalBudgetDTO dto = new PeriodicalBudgetDTO();
        dto.setYearIndex(source.getYearIndex());
        dto.setAmount(source.getAmount());
        dto.setPeriod(source.getPeriod().toString());
        return dto;
    }

    public Integer getYearIndex() {
        return yearIndex;
    }

    public void setYearIndex(Integer yearIndex) {
        this.yearIndex = yearIndex;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
