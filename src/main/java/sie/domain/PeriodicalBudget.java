package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.YearMonth;
import sie.io.JsonYearMonthSerializer;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class PeriodicalBudget implements Entity, Comparable<PeriodicalBudget> {

    private final Integer yearIndex;
    @JsonSerialize(using = JsonYearMonthSerializer.class)
    private final YearMonth period;
    private final BigDecimal amount;

    private PeriodicalBudget(Integer yearIndex, YearMonth period, BigDecimal amount) {
        this.yearIndex = yearIndex;
        this.period = period;
        this.amount = amount;
    }

    public static PeriodicalBudget of(Integer yearIndex, YearMonth period, BigDecimal amount) {
        return new PeriodicalBudget(yearIndex, period, amount);
    }

    public Integer getYearIndex() {
        return yearIndex;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public BigDecimal getAmount() {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public int compareTo(PeriodicalBudget other) {
        return this.getPeriod().compareTo(other.getPeriod());
    }

    @Override
    public String toString() {
        return "PeriodicalBudget{" + "period=" + period + ", amount=" + amount + '}';
    }

}
