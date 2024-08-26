package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import sie.io.YearMonthSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class PeriodicalBudget implements Entity, Comparable<PeriodicalBudget> {

    private final String line;
    private final Integer yearIndex;
    @JsonSerialize(using = YearMonthSerializer.class)
    private final YearMonth period;
    private final BigDecimal amount;

    private PeriodicalBudget(String line,
            Integer yearIndex,
            YearMonth period,
            BigDecimal amount) {
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
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public Integer yearIndex() {
        return yearIndex;
    }

    public YearMonth period() {
        return period;
    }

    public BigDecimal amount() {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    @Override
    public int compareTo(PeriodicalBudget other) {
        return this.period().compareTo(other.period());
    }

    @Override
    public String toString() {
        return "PeriodicalBudget{" + "period=" + period + ", amount=" + amount + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.yearIndex);
        hash = 79 * hash + Objects.hashCode(this.period);
        hash = 79 * hash + Objects.hashCode(this.amount);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeriodicalBudget other = (PeriodicalBudget) obj;
        if (!Objects.equals(this.yearIndex, other.yearIndex)) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        return Objects.equals(this.amount, other.amount);
    }

}
