package sie.domain;

import java.math.BigDecimal;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
public class Balance implements Entity {

    private final BigDecimal amount;
    private final Integer yearIndex;

    private Balance(BigDecimal amount, Integer yearIndex) {
        this.amount = amount;
        this.yearIndex = yearIndex;
    }

    public static Balance of(BigDecimal amount, Integer yearIndex) {
        return new Balance(amount, yearIndex);
    }

    public BigDecimal getAmount() {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    public Integer getYearIndex() {
        return yearIndex;
    }

    @Override
    public String toString() {
        return "Balance{"
                + "amount=" + amount + ", "
                + "yearIndex=" + yearIndex + '}';
    }
}
