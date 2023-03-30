package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import sie.domain.Account.ObjectId;
import sie.io.LocalDateSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class Transaction implements Entity {

    private final String line;
    private final String accountNumber;
    private final BigDecimal amount;
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate date;
    private final String text;
    private final Double quantity;
    private final String signature;
    private final List<ObjectId> objectIds;

    private Transaction(String line, String accountNumber, BigDecimal amount, LocalDate date, String text, Double quantity, String signature, List<ObjectId> objectIds) {
        this.line = line;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.date = date;
        this.text = text;
        this.quantity = quantity;
        this.signature = signature;
        this.objectIds = objectIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public String accountNumber() {
        return accountNumber;
    }

    public BigDecimal amount() {
        if (amount == null) {
            return null;
        }
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    public Optional<LocalDate> optDate() {
        return Optional.ofNullable(date);
    }

    public Optional<String> optText() {
        return Optional.ofNullable(text == null || text.isBlank() ? null : text);
    }

    public Optional<Double> optQuantity() {
        return Optional.ofNullable(quantity);
    }

    public Optional<String> getSignature() {
        return Optional.ofNullable(signature == null || signature.isBlank() ? null : signature);
    }

    public List<ObjectId> objectIds() {
        return new ArrayList<>(objectIds);
    }

    public List<String> costCentreIds() {
        return objectIds.stream()
                .filter(objId -> objId.dimensionId().equals(AccountingDimension.COST_CENTRE))
                .map(ObjectId::objectNumber)
                .collect(Collectors.toList());
    }

    public List<String> costBearerIds() {
        return objectIds.stream()
                .filter(objId -> objId.dimensionId().equals(AccountingDimension.COST_BEARER))
                .map(ObjectId::objectNumber)
                .collect(Collectors.toList());
    }

    public List<String> projectIds() {
        return objectIds.stream()
                .filter(objId -> objId.dimensionId().equals(AccountingDimension.PROJECT))
                .map(ObjectId::objectNumber)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "line=" + line + ", "
                + "accountNumber=" + accountNumber + ", "
                + "amount=" + amount + ", "
                + "date=" + date + ", "
                + "text=" + text + ", "
                + "quantity=" + quantity + ", "
                + "signature=" + signature + ", "
                + "objectIds=" + objectIds + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.accountNumber);
        hash = 19 * hash + Objects.hashCode(this.amount);
        hash = 19 * hash + Objects.hashCode(this.date);
        hash = 19 * hash + Objects.hashCode(this.text);
        hash = 19 * hash + Objects.hashCode(this.quantity);
        hash = 19 * hash + Objects.hashCode(this.signature);
        hash = 19 * hash + Objects.hashCode(this.objectIds);
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
        final Transaction other = (Transaction) obj;
        if (!Objects.equals(this.accountNumber, other.accountNumber)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.signature, other.signature)) {
            return false;
        }
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.quantity, other.quantity)) {
            return false;
        }
        return Objects.equals(this.objectIds, other.objectIds);
    }

    public static class Builder {

        private String line;
        private String accountNumber;
        private BigDecimal amount;
        private LocalDate date;
        private String text;
        private Double quantity;
        private String signature;
        private final List<ObjectId> objectIds = new ArrayList<>();

        private Builder() {
        }

        public Builder line(String line) {
            this.line = line;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder quantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder addObjectId(ObjectId id) {
            objectIds.add(id);
            return this;
        }

        public Transaction apply() {
            return new Transaction(line, accountNumber, amount, date, text, quantity, signature, objectIds);
        }
    }

}
