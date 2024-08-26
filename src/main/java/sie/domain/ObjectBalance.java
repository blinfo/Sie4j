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
public final class ObjectBalance implements Entity {

    private final String line;
    private final BigDecimal amount;
    private final Integer yearIndex;
    private final ObjectId objectId;
    private final Double quantity;

    private ObjectBalance(String line,
            BigDecimal amount,
            Integer yearIndex,
            ObjectId objectId,
            Double quantity) {
        this.line = line;
        this.amount = Objects.requireNonNull(amount);
        this.yearIndex = Objects.requireNonNull(yearIndex);
        this.objectId = Objects.requireNonNull(objectId);
        this.quantity = quantity;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    /**
     * Getter for the amount.
     *
     * @return BigDecimal - the Amount for the balance/result
     */
    public BigDecimal amount() {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Getter for the yearIndex.
     *
     * @return Integer - the YearIndex for the balance/result. Current year: 0,
     * previous year: -1 ...
     */
    public Integer yearIndex() {
        return yearIndex;
    }

    /**
     *
     * @return ObjectId
     */
    public ObjectId objectId() {
        return objectId;
    }

    /**
     *
     * @return Optional of Double - quantity for the object balance.
     */
    public Optional<Double> optQuantity() {
        return Optional.ofNullable(quantity);
    }

    @Override
    public String toString() {
        return "ObjectBalance{" + "amount=" + amount + ", yearIndex=" + yearIndex + ", objectId=" + objectId + ", quantity=" + quantity + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.amount);
        hash = 29 * hash + Objects.hashCode(this.yearIndex);
        hash = 29 * hash + Objects.hashCode(this.objectId);
        hash = 29 * hash + Objects.hashCode(this.quantity);
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
        final ObjectBalance other = (ObjectBalance) obj;
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        if (!Objects.equals(this.yearIndex, other.yearIndex)) {
            return false;
        }
        if (!Objects.equals(this.objectId, other.objectId)) {
            return false;
        }
        return Objects.equals(this.quantity, other.quantity);
    }

    public static class Builder {

        private String line;
        private BigDecimal amount;
        private Integer yearIndex;
        private ObjectId objectId;
        private Double quantity;

        private Builder() {
        }

        public Builder line(String line) {
            this.line = line;
            return this;
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
            return new ObjectBalance(line, amount, yearIndex, objectId, quantity);
        }
    }
}
