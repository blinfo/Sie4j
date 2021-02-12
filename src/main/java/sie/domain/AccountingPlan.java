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

    public static Builder builder() {
        return new Builder();
    }

    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }

    public List<Account> getAccounts() {
        return accounts.stream().sorted().collect(Collectors.toList());
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

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder accounts(List<Account> accounts) {
            this.accounts.clear();
            this.accounts.addAll(accounts);
            return this;
        }

        public AccountingPlan apply() {
            return new AccountingPlan(type, accounts);
        }
    }
}
