package sie.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import sie.domain.Account.ObjectId;

/**
 *
 * @author Håkan Lidén
 */
public class PeriodicalBalance implements Entity {

    private final String line;
    private final Integer yearIndex;
    private final YearMonth period;
    private final ObjectId objectId;
    private final BigDecimal amount;
    private final Double quantity;

    private PeriodicalBalance(String line, Integer yearIndex, YearMonth period, ObjectId objectId, BigDecimal amount, Double quantity) {
        this.line = line;
        this.yearIndex = yearIndex;
        this.period = period;
        this.objectId = objectId;
        this.amount = amount;
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
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

    public Optional<ObjectId> getObjectId() {
        return Optional.ofNullable(objectId);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Optional<Double> getQuantity() {
        return Optional.ofNullable(quantity);
    }

    @Override
    public String toString() {
        return "PeriodicalBalance{"
                + "yearIndex=" + yearIndex + ", "
                + "period=" + period + ", "
                + "objectId=" + objectId + ", "
                + "amount=" + amount + ", "
                + "quantity=" + quantity + '}';
    }

    public static class Builder {

        private String line;
        private Integer yearIndex;
        private YearMonth period;
        private ObjectId objectId;
        private BigDecimal amount;
        private Double quantity;

        private Builder() {
        }

        public Builder line(String line) {
            this.line = line;
            return this;
        }

        public Builder yearIndex(Integer yearIndex) {
            this.yearIndex = yearIndex;
            return this;
        }

        public Builder period(YearMonth period) {
            this.period = period;
            return this;
        }

        public Builder objectId(ObjectId objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder objectId(Integer dimensionId, String objectNumber) {
            return objectId(ObjectId.of(dimensionId, objectNumber));
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public PeriodicalBalance apply() {
            return new PeriodicalBalance(line, yearIndex, period, objectId, amount, quantity);
        }
    }
}
