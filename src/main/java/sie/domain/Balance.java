package sie.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * Balance are required for accounts in documents of types E1 through E4,
 * forbidden in document type I4. Apart from the amount, the balance contains a
 * year index. This refers to the financial years in the document. The current
 * year has index 0, previous year index -1, the year before that index -2 etc.
 * <p>
 * Balance represents opening and closing balance or result for the accounts.
 *
 *
 * @author Håkan Lidén
 *
 */
public class Balance implements Entity {

    private final String line;
    private final BigDecimal amount;
    private final Integer yearIndex;

    private Balance(String line, BigDecimal amount, Integer yearIndex) {
        this.line = line;
        this.amount = Objects.requireNonNull(amount);
        this.yearIndex = Objects.requireNonNull(yearIndex);
    }

    /**
     * Static instantiation of Balance.
     * <p>
     * Used for opening balance, closing balance and result.
     * <p>
     * SIE:<br>
     * Opening balance: <code>#IB <b>0</b> 2440 <b>-2380.39</b></code><br>
     * Closing balance: <code>#UB <b>0</b> 2440 <b>-3189.11</b></code><br>
     * Result: <code>#RES <b>0</b> 3011 <b>-23780.78</b></code>
     *
     * @param amount Required
     * @param yearIndex Required
     * @return Balance - the balance/result
     * @throws NullPointerException if either or both parameters are null.
     */
    public static Balance of(BigDecimal amount, Integer yearIndex) {
        return of(null, amount, yearIndex);
    }

    public static Balance of(String line, BigDecimal amount, Integer yearIndex) {
        return new Balance(line, amount, yearIndex);
    }

    @Override
    public Optional<String> getLine() {
        return Optional.ofNullable(line);
    }

    /**
     * Getter for the amount.
     *
     * @return BigDecimal - the Amount for the balance/result
     */
    public BigDecimal getAmount() {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Getter for the yearIndex.
     *
     * @return Integer - the YearIndex for the balance/result. Current year: 0,
     * previous year: -1 ...
     */
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
