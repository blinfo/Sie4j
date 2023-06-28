package sie.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class Voucher implements Entity, Comparable<Voucher> {

    private final String line;
    private final String series;
    private final Integer number;
    private final LocalDate date;
    private final String text;
    private final LocalDate registrationDate;
    private final String signature;
    private final List<Transaction> transactions;

    private Voucher(String line, String series, Integer number, LocalDate date, String text, LocalDate registrationDate, String signature, List<Transaction> transactions) {
        this.line = line;
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

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public Optional<String> optSeries() {
        return Optional.ofNullable(series == null || series.isBlank() ? null : series);
    }

    public Voucher series(String newValue) {
        return new Voucher(line, newValue, number, date, text, registrationDate, signature, transactions);
    }

    public Optional<Integer> optNumber() {
        return Optional.ofNullable(number);
    }

    public Voucher number(Integer newValue) {
        return new Voucher(line, series, newValue, date, text, registrationDate, signature, transactions);
    }

    public LocalDate date() {
        return date;
    }

    public Optional<String> optText() {
        return Optional.ofNullable(text == null || text.isBlank() ? null : text);
    }

    public Optional<LocalDate> optRegistrationDate() {
        return Optional.ofNullable(registrationDate);
    }

    public Optional<String> optSignature() {
        return Optional.ofNullable(signature == null || signature.isBlank() ? null : signature);
    }

    public List<Transaction> transactions() {
        return new ArrayList<>(transactions);
    }

    public Boolean balanced() {
        return diff().equals(BigDecimal.ZERO.setScale(Entity.SCALE));
    }

    public BigDecimal diff() {
        return new BigDecimal(transactions().stream()
                .mapToDouble(t -> t.amount().doubleValue()).sum())
                .setScale(Entity.SCALE, Entity.ROUNDING_MODE);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.series);
        hash = 47 * hash + Objects.hashCode(this.number);
        hash = 47 * hash + Objects.hashCode(this.date);
        hash = 47 * hash + Objects.hashCode(this.text);
        hash = 47 * hash + Objects.hashCode(this.registrationDate);
        hash = 47 * hash + Objects.hashCode(this.signature);
        hash = 47 * hash + Objects.hashCode(this.transactions);
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
        final Voucher other = (Voucher) obj;
        if (!Objects.equals(this.series, other.series)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.signature, other.signature)) {
            return false;
        }
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.registrationDate, other.registrationDate)) {
            return false;
        }
        return Objects.equals(this.transactions, other.transactions);
    }

    @Override
    public int compareTo(Voucher other) {
        int result = 0;
        if (optSeries().isPresent() && other.optSeries().isPresent()) {
            result = optSeries().get().compareTo(other.optSeries().get());
        }
        if (result == 0 && optNumber().isPresent() && other.optNumber().isPresent()) {
            result = optNumber().get().compareTo(other.optNumber().get());
        }
        if (result == 0 && date() != null && other.date() != null) {
            result = date().compareTo(other.date());
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
        private final List<Transaction> transactions = new ArrayList<>();
        private String line;

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

        public Builder line(String line) {
            this.line = line;
            return this;
        }

        public Voucher apply() {
            return new Voucher(line, series, number, date, text, registrationDate, signature, transactions);
        }

    }
}
