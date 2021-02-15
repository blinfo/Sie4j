package sie.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import sie.domain.Account.ObjectId;

/**
 *
 * @author hakan
 */
public class PeriodicalBalance {

    private final Integer yearIndex;
    private final YearMonth period;
    private final ObjectId objectId;
    private final BigDecimal amount;
    private final Double quantity;

    private PeriodicalBalance(Integer yearIndex, YearMonth period, ObjectId objectId, BigDecimal amount, Double quantity) {
        this.yearIndex = yearIndex;
        this.period = period;
        this.objectId = objectId;
        this.amount = amount;
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
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
        return "PeriodicalBalance{" + 
                "yearIndex=" + yearIndex + ", "
                + "period=" + period + ", "
                + "objectId=" + objectId + ", "
                + "amount=" + amount + ", "
                + "quantity=" + quantity + '}';
    }
    

    public static class Builder {

        private Integer yearIndex;
        private YearMonth period;
        private ObjectId objectId;
        private BigDecimal amount;
        private Double quantity;

        private Builder() {
        }

        public Builder yearIndex(Integer yearIndex) {
            this.yearIndex = yearIndex;
            return this;
        }

        public Builder period(YearMonth period) {
            this.period = period;
            return this;
        }

        public Builder specification(ObjectId objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder specification(Integer dimensionId, String objectNumber) {
            this.objectId = ObjectId.of(dimensionId, objectNumber);
            return this;
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
            return new PeriodicalBalance(yearIndex, period, objectId, amount, quantity);
        }
    }
}
