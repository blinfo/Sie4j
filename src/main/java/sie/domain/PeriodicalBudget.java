package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import sie.io.YearMonthSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public class PeriodicalBudget implements Entity, Comparable<PeriodicalBudget> {

    private final String line;
    private final Integer yearIndex;
    @JsonSerialize(using = YearMonthSerializer.class)
    private final YearMonth period;
    private final BigDecimal amount;

    private PeriodicalBudget(String line, Integer yearIndex, YearMonth period, BigDecimal amount) {
        this.line = line;
        this.yearIndex = yearIndex;
        this.period = period;
        this.amount = amount;
    }

    public static PeriodicalBudget of(Integer yearIndex, YearMonth period, BigDecimal amount) {
        return of(null, yearIndex, period, amount);
    }

    public static PeriodicalBudget of(String line, Integer yearIndex, YearMonth period, BigDecimal amount) {
        return new PeriodicalBudget(line, yearIndex, period, amount);
    }

    @Override
    public Optional<String> getLine() {
        return Optional.ofNullable(line);
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
