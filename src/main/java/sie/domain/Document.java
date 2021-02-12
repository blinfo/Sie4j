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
public class Document implements Entity {

    private final MetaData metaData;
    private final AccountingPlan accountingPlan;
    private final List<Voucher> vouchers;
    private final String checksum;

    private Document(MetaData metaData, AccountingPlan accountingPlan, List<Voucher> vouchers, String checksum) {
        this.metaData = metaData;
        this.accountingPlan = accountingPlan;
        this.vouchers = vouchers;
        this.checksum = checksum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Optional<AccountingPlan> getAccountingPlan() {
        return Optional.ofNullable(accountingPlan);
    }

    public List<Voucher> getVouchers() {
        return vouchers.stream().sorted().collect(Collectors.toList());
    }

    public List<Voucher> getImbalancedVouchers() {
        return vouchers.stream().filter(voucher -> !voucher.isBalanced()).collect(Collectors.toList());
    }

    public Boolean isBalanced() {
        return getImbalancedVouchers().isEmpty();
    }

    public Optional<String> getChecksum() {
        return Optional.ofNullable(checksum);
    }

    @Override
    public String toString() {
        return "Document{"
                + "metaData=" + metaData + ", "
                + "accountingPlan=" + accountingPlan + ", "
                + "vouchers=" + vouchers + '}';
    }

    public static class Builder {

        private MetaData metaData;
        private AccountingPlan accountingPlan;
        private List<Voucher> vouchers = new ArrayList<>();
        private String checksum;

        public Builder() {
        }

        public Builder metaData(MetaData metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder accountingPlan(AccountingPlan accountingPlan) {
            this.accountingPlan = accountingPlan;
            return this;
        }

        public Builder vouchers(List<Voucher> vouchers) {
            this.vouchers = vouchers;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public Document apply() {
            return new Document(metaData, accountingPlan, vouchers, checksum);
        }
    }

    /**
     * The type of document.
     * <p>
     * The SIE standard has five types of documents:
     * <ul>
     * <li>E1 - Export of balances for annual accounts
     * <li>E2 - Export of balances for periodical accounts
     * <li>E3 - Export of balances for object accounts
     * <li>E4 - Export of accounts, vouchers and transactions
     * <li>I4 - Import of accounts, vouchers and transactions
     * </ul>
     * <p>
     * If the type is omitted it is assumed that the type in question is E1.
     * <p>
     * Default is Document.Type.E1
     *
     */
    public enum Type {
        /**
         * Export of balances for annual accounts.
         * <p>
         * This type is typically used for exporting data to systems handling
         * tax returns et al.
         */
        E1,
        /**
         * Export of balances for periodical accounts.
         */
        E2,
        /**
         * Export of balances for object accounts.
         */
        E3,
        /**
         * Export of accounts, vouchers and transactions.
         * <p>
         * This type contains the most complete representation of the accounting
         * data.
         */
        E4,
        /**
         * Import of accounts, vouchers and transactions.
         * <p>
         * Accounts are optional for documents of this type.
         */
        I4;

        /**
         * Returns the number part of the type.
         * <p>
         * Values are number 1 through 4. Note that E4 and I4 returns the same
         * number.
         * <p>
         * This type is most commonly used by external pre-processing systems,
         * i.e. invoicing-programs, cashier-systems et al.
         *
         * @return The number part of the Type
         */
        public String getNumber() {
            return name().replaceAll("\\D", "");
        }
    }
}