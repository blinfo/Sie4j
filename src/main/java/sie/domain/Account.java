package sie.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Account implements Entity, Comparable<Account> {

    private static final Pattern NUMBER = Pattern.compile("\\d+");
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

    /**
     * Static instantiation of the Account.Builder.
     * <p>
     *
     * @return Account.Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for the number of the account.
     *
     * @return String the number of the account.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Getter for the account number as an optional integer.
     * <p>
     * This method attempts to cast the account number to an Integer. Since some
     * account numbers may (rarely) be represented by letters as well as digits,
     * the method returns an optional of an Integer if the number consists
     * solely of digits, otherwise an empty optional is returned.
     *
     * @return Optional of the account number as an Integer.
     */
    public Optional<Integer> getNumberAsInteger() {
        return Optional.ofNullable(NUMBER.matcher(number).matches() ? Integer.valueOf(number) : null);
    }

    /**
     * Getter for the account label.
     * <p>
     * This method returns an optional String representing the label, or an
     * empty optional if the label is missing.
     *
     * @return Optional of the account label.
     */
    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    /**
     * Getter for the account type.
     *
     * @return Optional of the Account Type.
     */
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * Getter for the unit used with this account.
     *
     * @return String unit
     */
    public Optional<String> getUnit() {
        return Optional.ofNullable(unit);
    }

    /**
     * Getter for SRU codes.
     * <p>
     * The SRU codes are used in annual reports and can be found at:
     * <a href="https://www.bas.se/" target="bas">https://www.bas.se/</a> - BAS
     * gruppen.
     * <p>
     * SRU codes are required for types E1 and E2, optional in the other.
     *
     * @return List of SRU codes
     */
    public List<String> getSruCodes() {
        return sruCodes;
    }

    /**
     * Getter for the opening balances<sup>*</sup>.
     * <p>
     * Opening balance are used for balance-accounts, most commonly accounts
     * numbers 1000 through 2999. If this field is used it contains values for
     * each financial year included in the document.
     * <p>
     * <sup>*</sup> Opening balances should be provided for all SIE types except
     * for type I4, where they are not allowed.
     *
     * @return List of opening balances
     */
    public List<Balance> getOpeningBalances() {
        return openingBalances;
    }

    /**
     * Getter for opening balance<sup>*</sup>.
     * <p>
     * This method returns an optional opening balance associated with the year
     * index provided, or an empty optional if no balance is found for the year
     * index.
     * <p>
     * <sup>*</sup> Opening balances should be provided for all SIE types except
     * for type I4, where they are not allowed.
     *
     * @param Integer yearIndex
     * @return Optional of the Balance
     */
    public Optional<Balance> getOpeningBalanceByYearIndex(Integer yearIndex) {
        return openingBalances.stream().filter(b -> b.getYearIndex().equals(yearIndex)).findFirst();
    }

    /**
     * Getter for the opening balances<sup>*</sup>.
     * <p>
     * Closing balance are used for balance-accounts, most commonly accounts
     * numbers 1000 through 2999. If this field is used it contains values for
     * each financial year included in the document.
     * <p>
     * <sup>*</sup> Closing balances should be provided for all SIE types except
     * for type I4, where are not allowed.
     *
     * @return List of opening balances
     */
    public List<Balance> getClosingBalances() {
        return closingBalances;
    }

    /**
     * Getter for closing balance<sup>*</sup>.
     * <p>
     * This method returns an optional closing balance associated with the year
     * index provided, or an empty optional if no balance is found for the year
     * index.
     * <p>
     * <sup>*</sup> Closing balances should be provided for all SIE types except
     * for type I4, where they are not allowed.
     *
     * @param Integer yearIndex
     * @return Optional of the balance
     */
    public Optional<Balance> getClosingBalanceByYearIndex(Integer yearIndex) {
        return closingBalances.stream().filter(b -> b.getYearIndex().equals(yearIndex)).findFirst();
    }

    /**
     * Getter for the results<sup>*</sup>.
     * <p>
     * Results are used for result-accounts, most commonly accounts with numbers
     * greater than 2999. If this field is used it contains values for each
     * financial year included in the document.
     * <p>
     * <sup>*</sup> Results should be provided for all SIE types except for type
     * I4, where they are not allowed.
     *
     * @return List of results
     */
    public List<Balance> getResults() {
        return results;
    }

    /**
     * Getter for closing balance<sup>*</sup>.
     * <p>
     * This method returns an optional result associated with the year index
     * provided, or an empty optional if no balance is found for the year index.
     * <p>
     * <sup>*</sup> Results should be provided for all SIE types except for type
     * I4, where they are not allowed.
     *
     * @param Integer yearIndex
     * @return Optional of the result
     */
    public Optional<Balance> getResultByYearIndex(Integer yearIndex) {
        return results.stream().filter(b -> b.getYearIndex().equals(yearIndex)).findFirst();
    }

    /**
     * Getter for periodical budgets<sup>*</sup>.
     * <p>
     * <sup>*</sup> Must be provided in Types E2 and E3, may occur in Type E4
     * and are not allowed in Types E1 and I4.
     *
     * @return List of periodical budgets
     */
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

        /**
         * Required - Account number.
         * <p>
         * Account numbers are most commonly 4 digits, but other "numbers" are
         * allowed, such as a combination of numbers and letters.
         * <p>
         * SIE: <code>#KONTO <b>1910</b> Kassa</code>
         *
         * @param number the account number: number("1910")
         * @return Account.Builder
         */
        public Builder number(String number) {
            this.number = number;
            return this;
        }

        /**
         * Required - Account number.
         * <p>
         * Convenience method for account numbers that are Integers. This method
         * calls the number(String number) method with toString on the integer.
         * Use either of them, not both.
         *
         * @param number the account number: number(1910)
         * @return Account.Builder
         */
        public Builder number(Integer number) {
            return number(number.toString());
        }

        /**
         * Optional - The label for the account.
         * <p>
         * The label is a short description of the account.
         * <p>
         * SIE: <code>#KONTO 1910 <b>Kassa</b></code>
         *
         * @param label the label: label("Kassa")
         * @return Account.Builder
         */
        public Builder label(String label) {
            this.label = label;
            return this;
        }

        /**
         * Optional - Account type.
         * <p>
         * This field is optional, and the type can most often be derived from
         * the number. For more information:
         * <a href="https://www.bas.se/" target="bas">https://www.bas.se/</a> -
         * BAS gruppen.
         *
         * <p>
         * SIE: <code>#KTYP 1910 <b>T</b></code>
         *
         * @param type the account type: type(Account.Type.T)
         * @return Account.Builder
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * Optional - Account unit.
         * <p>
         * The unit can be "liter", "st." et al.
         * <p>
         * SIE: <code>#ENHET 4010 <b>liter</b></code>
         *
         * @param unit the unit used: ("liter")
         * @return Account.Builder
         */
        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        /**
         * Optional - Account SRU Code.
         * <p>
         * The SRU codes are used in annual reports and can be found at:
         * <a href="https://www.bas.se/" target="bas">https://www.bas.se/</a> -
         * BAS gruppen.
         * <p>
         * Note that some accounts may have more than one SRU code.
         * <p>
         * SIE: <code>#SRU 1910 <b>7214</b></code>
         *
         * @param sruCode the SRU code for the account: addSruCode("7214")
         * @return Account.Builder
         */
        public Builder addSruCode(String sruCode) {
            this.sruCodes.add(sruCode);
            return this;
        }

        /**
         * Optional<sup>*</sup> - Opening balance.
         * <p>
         * Opening balance are used for balance-accounts, most commonly accounts
         * numbers 1000 through 2999. If this field is used it must contain
         * values for each financial year included in the document.
         * <p>
         * <sup>*</sup> Opening balance should be provided for all SIE types
         * except for type I4, where it is not allowed.
         * <p>
         * SIE: <code>#IB 0 1930 23780.78</code>
         *
         * @param balance
         * @return Account.Builder
         */
        public Builder addOpeningBalance(Balance balance) {
            openingBalances.add(balance);
            return this;
        }

        /**
         * Optional<sup>*</sup> - Closing balance.
         * <p>
         * Opening balance are used for balance-accounts, most commonly accounts
         * numbers 1000 through 2999. If this field is used it must contain
         * values for each financial year included in the document.
         * <p>
         * <sup>*</sup> Closing balance should be provided for all SIE types
         * except for type I4, where it is not allowed.
         * <p>
         * SIE: <code>#UB 0 2440 -2380.39</code>
         *
         * @param balance
         * @return Account.Builder
         */
        public Builder addClosingBalance(Balance balance) {
            closingBalances.add(balance);
            return this;
        }

        /**
         * Optional<sup>*</sup> - Result.
         * <p>
         * Opening balance are used for balance-accounts, most commonly accounts
         * numbers higher than 2999. If this field is used it must contain
         * values for each financial year included in the document.
         * <p>
         * <sup>*</sup> Result should be provided for all SIE types except for
         * type I4, where it is not allowed.
         * <p>
         * SIE: <code>#RES 0 3011 -23780.78</code>
         *
         * @param balance
         * @return Account.Builder
         */
        public Builder addResult(Balance balance) {
            results.add(balance);
            return this;
        }

        /**
         * Optional<sup>*</sup> - Periodical Budget.
         * <p>
         * <sup>*</sup> Must be present in Types E2 and E3, may occur in Type E4
         * and are not allowed in Types E1 and I4.
         *
         * <p>
         * SIE: <code>#PBUDGET 0 200801 3011 {} -1243.50 -415</code><br/>
         * SIE: <code>#PBUDGET 0 200801 5010 {1 "0123"} 3411.80</code>
         *
         * @param budget
         * @return
         */
        public Builder addPeriodicalBudget(PeriodicalBudget budget) {
            periodicalBudgets.add(budget);
            return this;
        }

        /**
         *
         * @return Account representing the data in the builder.
         */
        public Account apply() {
            return new Account(number, label, type, unit, sruCodes, openingBalances, closingBalances, results, periodicalBudgets);
        }

    }

    public enum Type {
        /**
         * Income (Intäkt)
         */
        I,
        /**
         * Cost (Kostnad)
         */
        K,
        /**
         * Debt (Skuld)
         */
        S,
        /**
         * Asset (Tillgång)
         */
        T;

        /**
         * Find type by string.
         * <p>
         * Returns an optional of the type matching the string, or an empty
         * optional if no type is found.
         *
         * @param string String to be found: Type.find("T")
         * @return Optional of Type
         */
        public static Optional<Type> find(String string) {
            try {
                return Optional.of(valueOf(string));
            } catch (IllegalArgumentException | NullPointerException ex) {
                return Optional.empty();
            }
        }
    }
}
