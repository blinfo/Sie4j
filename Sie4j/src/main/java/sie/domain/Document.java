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
public class Document {

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

    public enum Type {
        E1, E2, E3, E4, I4;

        public String getNumber() {
            return name().replaceAll("\\D", "");
        }
    }
}
