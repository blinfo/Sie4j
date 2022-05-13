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
public class Transaction implements Entity {

    private final String accountNumber;
    private final BigDecimal amount;
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate date;
    private final String text;
    private final Double quantity;
    private final String signature;
    private final List<ObjectId> objectIds;

    private Transaction(String accountNumber, BigDecimal amount, LocalDate date, String text, Double quantity, String signature, List<ObjectId> objectIds) {
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        if (amount == null) {
            return null;
        }
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(date);
    }

    public Optional<String> getText() {
        return Optional.ofNullable(text == null || text.isBlank() ? null : text);
    }

    public Optional<Double> getQuantity() {
        return Optional.ofNullable(quantity);
    }

    public Optional<String> getSignature() {
        return Optional.ofNullable(signature == null || signature.isBlank() ? null : signature);
    }

    public List<ObjectId> getObjectIds() {
        return new ArrayList<>(objectIds);
    }

    public List<String> getCostCentreIds() {
        return objectIds.stream()
                .filter(objId -> objId.getDimensionId().equals(AccountingDimension.COST_CENTRE))
                .map(ObjectId::getObjectNumber)
                .collect(Collectors.toList());
    }

    public List<String> getCostBearerIds() {
        return objectIds.stream()
                .filter(objId -> objId.getDimensionId().equals(AccountingDimension.COST_BEARER))
                .map(ObjectId::getObjectNumber)
                .collect(Collectors.toList());
    }

    public List<String> getProjectIds() {
        return objectIds.stream()
                .filter(objId -> objId.getDimensionId().equals(AccountingDimension.PROJECT))
                .map(ObjectId::getObjectNumber)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Transaction{" + "accountNumber=" + accountNumber + ", "
                + "amount=" + amount + ", "
                + "date=" + date + ", "
                + "text=" + text + ", "
                + "quantity=" + quantity + ", "
                + "signature=" + signature + ", "
                + "objectIds=" + objectIds + '}';
    }

    public static class Builder {

        private String accountNumber;
        private BigDecimal amount;
        private LocalDate date;
        private String text;
        private Double quantity;
        private String signature;
        private final List<ObjectId> objectIds = new ArrayList<>();

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
            return new Transaction(accountNumber, amount, date, text, quantity, signature, objectIds);
        }
    }

}
