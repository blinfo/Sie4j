package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sie.io.JsonDateSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Voucher implements Entity, Comparable<Voucher> {

    private static final double DELTA = 0.005;
    private final String series;
    private final Integer number;
    @JsonSerialize(using = JsonDateSerializer.class)
    private final LocalDate date;
    private final String text;
    @JsonSerialize(using = JsonDateSerializer.class)
    private final LocalDate registrationDate;
    private final String signature;
    private final List<Transaction> transactions;

    private Voucher(String series, Integer number, LocalDate date, String text, LocalDate registrationDate, String signature, List<Transaction> transactions) {
        this.series = series;
        this.number = number;
        this.date = date;
        this.text = text;
        this.registrationDate = registrationDate;
        this.signature = signature;
        this.transactions = transactions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<String> getSeries() {
        return Optional.ofNullable(series);
    }

    public Optional<Integer> getNumber() {
        return Optional.ofNullable(number);
    }

    public LocalDate getDate() {
        return date;
    }

    public Optional<String> getText() {
        return Optional.ofNullable(text);
    }

    public Optional<LocalDate> getRegistrationDate() {
        return Optional.ofNullable(registrationDate);
    }

    public Optional<String> getSignature() {
        return Optional.ofNullable(signature);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public Boolean isBalanced() {
        return getDiff().equals(BigDecimal.ZERO);
    }

    public BigDecimal getDiff() {
        BigDecimal sum = new BigDecimal(getTransactions().stream()
                .mapToDouble(t -> t.getAmount().doubleValue()).sum());
        if (sum.abs().doubleValue() <= DELTA) {
            return BigDecimal.ZERO;
        }
        return sum.setScale(Entity.SCALE, Entity.ROUNDING_MODE);
    }

    @Override
    public int compareTo(Voucher other) {
        int result = 0;
        if (getSeries().isPresent() && other.getSeries().isPresent()) {
            result = getSeries().get().compareTo(other.getSeries().get());
        }
        if (result == 0 && getNumber().isPresent() && other.getNumber().isPresent()) {
            result = getNumber().get().compareTo(other.getNumber().get());
        }
        if (result == 0) {
            result = getDate().compareTo(other.getDate());
        }
        return result;
    }

    @Override
    public String toString() {
        return "Voucher{"
                + "series=" + series + ", "
                + "number=" + number + ", "
                + "date=" + date + ", "
                + "comment=" + text + ", "
                + "registrationDate=" + registrationDate + ", "
                + "signature=" + signature + ", "
                + "transactions=" + transactions + '}';
    }

    public static class Builder {

        private String series;
        private Integer number;
        private LocalDate date;
        private String text;
        private LocalDate registrationDate;
        private String signature;
        private List<Transaction> transactions = new ArrayList<>();

        private Builder() {
        }

        public Builder series(String series) {
            this.series = series;
            return this;
        }

        public Builder number(Integer number) {
            this.number = number;
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

        public Builder registrationDate(LocalDate registrationDate) {
            this.registrationDate = registrationDate;
            return this;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder addTransaction(Transaction transaction) {
            this.transactions.add(transaction);
            return this;
        }

        public Voucher apply() {
            return new Voucher(series, number, date, text, registrationDate, signature, transactions);
        }

    }
}
