package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.Entity;
import sie.domain.PeriodicalBudget;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "yearMonth", "amount"})
public class PeriodicalBudgetDTO implements DTO {

    private final PeriodicalBudget source;

    private PeriodicalBudgetDTO(PeriodicalBudget source) {
        this.source = source;
    }

    public static PeriodicalBudgetDTO from(PeriodicalBudget source) {
        return new PeriodicalBudgetDTO(source);
    }

    public Integer getYearIndex() {
        return source.getYearIndex();
    }

    public String getYearMonth() {
        return source.getPeriod().format(Entity.YEAR_MONTH_FORMAT);
    }

    public BigDecimal getAmount() {
        return source.getAmount();
    }
}
