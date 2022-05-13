package sie.domain;

import java.math.BigDecimal;
import java.util.*;
import sie.domain.Account.ObjectId;

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
public class ObjectBalance implements Entity {

    private final BigDecimal amount;
    private final Integer yearIndex;
    private final ObjectId objectId;
    private final Double quantity;

    private ObjectBalance(BigDecimal amount, Integer yearIndex, ObjectId objectId, Double quantity) {
        this.amount = Objects.requireNonNull(amount);
        this.yearIndex = Objects.requireNonNull(yearIndex);
        this.objectId = Objects.requireNonNull(objectId);
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
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

    /**
     *
     * @return ObjectId
     */
    public ObjectId getObjectId() {
        return objectId;
    }

    /**
     *
     * @return Optional of Double - quantity for the object balance.
     */
    public Optional<Double> getQuantity() {
        return Optional.ofNullable(quantity);
    }

    @Override
    public String toString() {
        return "ObjectBalance{" + "amount=" + amount + ", yearIndex=" + yearIndex + ", objectId=" + objectId + ", quantity=" + quantity + '}';
    }

    public static class Builder {

        private BigDecimal amount;
        private Integer yearIndex;
        private ObjectId objectId;
        private Double quantity;

        private Builder() {
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder yearIndex(Integer yearIndex) {
            this.yearIndex = yearIndex;
            return this;
        }

        public Builder objectId(ObjectId objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder objectId(Integer dimensionId, String objectNumber) {
            this.objectId = ObjectId.of(dimensionId, objectNumber);
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public ObjectBalance apply() {
            return new ObjectBalance(amount, yearIndex, objectId, quantity);
        }
    }
}
