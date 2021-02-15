package sie.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén
 *
 */
public class AccountingPlan implements Entity {

    private final String type;
    private final List<Account> accounts;

    private AccountingPlan(String type, List<Account> accounts) {
        this.type = type;
        this.accounts = accounts;
    }

    /**
     * Static instantiation of the AccountingPlan.Builder.
     *
     * @return AccountingPlan.Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for the type of accounting plan.
     * <p>
     * Returns an Optional string with the name of the type of accounting plan
     * used - e.g. EUBAS97 - or an empty optional if the name is absent.
     *
     * @return Optional String - The name of the type of accounting plan
     */
    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * Getter for the accounts.
     * <p>
     * Returns a list of the accounts used in the document. If no accounts are
     * present, an empty list will be returned.
     *
     * @return List Account - The accounts used in the document.
     */
    public List<Account> getAccounts() {
        return accounts.stream().sorted().collect(Collectors.toList());
    }

    public Optional<Account> getByNumber(String number) {
        return accounts.stream().filter(a -> a.getNumber().equals(number)).findFirst();
    }

    public Optional<Account> getByNumber(Integer number) {
        return getByNumber(number.toString());
    }

    @Override
    public String toString() {
        return "AccountingPlan{" + "accounts=" + accounts + '}';
    }

    public static class Builder {

        private String type;
        private final List<Account> accounts = new ArrayList<>();

        private Builder() {
        }

        /**
         * Optional - Accounting plan type.
         * <p>
         * The name of the type of accounting plan used, e.g. EUBAS97.
         *
         * @param type String e.g. EUBAS97
         * @return AccountingPlan.Builder
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * Required<sup>*</sup> - Setter for the list of accounts.
         * <p>
         * Takes a list of all accounts used in the document.
         * <p>
         * <sup>*</sup> The account list is required for all types of document
         * except for type I4, where it is optional.
         *
         * @param accounts List of accounts
         * @return AccountingPlan.Builder
         */
        public Builder accounts(List<Account> accounts) {
            this.accounts.clear();
            this.accounts.addAll(accounts);
            return this;
        }

        /**
         *
         * @return Accounting Plan representing the data in the builder.
         */
        public AccountingPlan apply() {
            return new AccountingPlan(type, accounts);
        }
    }
}
