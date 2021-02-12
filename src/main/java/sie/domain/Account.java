package sie.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sie.SieException;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Account implements Entity, Comparable<Account> {

    private final String number;
    private final String label;
    private final Type type;
    private final String unit;
    private final List<String> sruCodes;
    private final List<Balance> openingBalances;
    private final List<Balance> closingBalances;
    private final List<Balance> results;
    private final List<PeriodicalBudget> periodicalBudgets;

    private Account(String number, String label, Type type, String unit,
            List<String> sruCodes, List<Balance> openingBalances,
            List<Balance> closingBalances, List<Balance> results,
            List<PeriodicalBudget> periodicalBudgets) {
        this.number = number;
        this.label = label;
        this.type = type;
        this.unit = unit;
        this.sruCodes = sruCodes;
        this.openingBalances = openingBalances;
        this.closingBalances = closingBalances;
        this.results = results;
        this.periodicalBudgets = periodicalBudgets;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getNumber() {
        return number;
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    public Optional<String> getUnit() {
        return Optional.ofNullable(unit);
    }

    public List<String> getSruCodes() {
        return sruCodes;
    }

    public List<Balance> getOpeningBalances() {
        return openingBalances;
    }

    public Balance getOpeningBalanceByYearIndex(Integer yearIndex) {
        return openingBalances.stream().filter(b -> b.getYearIndex().equals(yearIndex)).findFirst().orElseThrow(() -> new SieException("No Closing Balance for year " + yearIndex));
    }

    public List<Balance> getClosingBalances() {
        return closingBalances;
    }

    public Balance getClosingBalanceByYearIndex(Integer yearIndex) {
        return closingBalances.stream().filter(b -> b.getYearIndex().equals(yearIndex)).findFirst().orElseThrow(() -> new SieException("No Closing Balance for year " + yearIndex));
    }

    public List<Balance> getResults() {
        return results;
    }

    public Balance getResultByYearIndex(Integer yearIndex) {
        return results.stream().filter(b -> b.getYearIndex().equals(yearIndex)).findFirst().orElseThrow(() -> new SieException("No Result for year " + yearIndex));
    }

    public List<PeriodicalBudget> getPeriodicalBudgets() {
        return periodicalBudgets.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public int compareTo(Account other) {
        return getNumber().compareTo(other.getNumber());
    }

    @Override
    public String toString() {
        return "Account{"
                + "number=" + number + ", "
                + "label=" + label + ", "
                + "type=" + type + ", "
                + "unit=" + unit + ", "
                + "sruCodes=" + sruCodes + ", "
                + "openingBalances=" + openingBalances + ", "
                + "closingBalances=" + closingBalances + ", "
                + "results=" + results + ", "
                + "periodicalBudgets=" + periodicalBudgets + '}';
    }

    public static class Builder {

        private String number;
        private String label;
        private Type type;
        private String unit;
        private final List<String> sruCodes = new ArrayList<>();
        private final List<Balance> openingBalances = new ArrayList<>();
        private final List<Balance> closingBalances = new ArrayList<>();
        private final List<Balance> results = new ArrayList<>();
        private final List<PeriodicalBudget> periodicalBudgets = new ArrayList<>();

        private Builder() {
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder sruCode(String sruCode) {
            this.sruCodes.add(sruCode);
            return this;
        }

        public Builder openingBalance(Balance balance) {
            openingBalances.add(balance);
            return this;
        }

        public Builder closingBalance(Balance balance) {
            closingBalances.add(balance);
            return this;
        }

        public Builder result(Balance balance) {
            results.add(balance);
            return this;
        }

        public Builder periodicalBudget(PeriodicalBudget budget) {
            periodicalBudgets.add(budget);
            return this;
        }

        public Account apply() {
            return new Account(number, label, type, unit, sruCodes, openingBalances, closingBalances, results, periodicalBudgets);
        }

    }

    public enum Type {
        I, K, S, T;

        public static Optional<Type> find(String string) {
            try {
                return Optional.of(valueOf(string));
            } catch (IllegalArgumentException | NullPointerException ex) {
                return Optional.empty();
            }
        }
    }
}
