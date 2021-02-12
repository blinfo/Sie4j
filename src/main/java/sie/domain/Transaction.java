package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sie.io.JsonDateSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class Transaction implements Entity {

    private final String accountNumber;
    private final BigDecimal amount;
    @JsonSerialize(using = JsonDateSerializer.class)
    private final LocalDate date;
    private final String text;
    private final Double quantity;
    private final String signature;

    private Transaction(String accountNumber, BigDecimal amount, LocalDate date, String text, Double quantity, String signature) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.date = date;
        this.text = text;
        this.quantity = quantity;
        this.signature = signature;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount.setScale(SCALE, ROUNDING_MODE);
    }

    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(date);
    }

    public Optional<String> getText() {
        return Optional.ofNullable(text);
    }

    public Optional<Double> getQuantity() {
        return Optional.ofNullable(quantity);
    }

    public Optional<String> getSignature() {
        return Optional.ofNullable(signature);
    }

    @Override
    public String toString() {
        return "Transaction{" 
                + "accountNumber=" + accountNumber + ", "
                + "amount=" + amount + ", "
                + "date=" + date + ", "
                + "text=" + text + ", "
                + "quantity=" + quantity + ", "
                + "signature=" + signature + '}';
    }

    public static class Builder {

        private String accountNumber;
        private BigDecimal amount;
        private LocalDate date;
        private String text;
        private Double quantity;
        private String signature;

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

        public Transaction apply() {
            return new Transaction(accountNumber, amount, date, text, quantity, signature);
        }
    }

}
