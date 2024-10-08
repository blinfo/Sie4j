package sie.domain;

import java.util.*;
import java.util.stream.Collectors;
import sie.Sie4j;
import sie.exception.*;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class Document implements Entity {

    private final MetaData metaData;
    private final AccountingPlan accountingPlan;
    private final List<Voucher> vouchers;
    private final List<AccountingDimension> dimensions;
    private final List<AccountingObject> objects;
    private final String checksum;

    private Document(MetaData metaData, AccountingPlan accountingPlan,
            List<Voucher> vouchers, List<AccountingDimension> dimensions,
            List<AccountingObject> objects, String checksum) {
        this.metaData = metaData;
        this.accountingPlan = accountingPlan;
        this.vouchers = vouchers;
        this.dimensions = dimensions;
        this.objects = objects;
        this.checksum = checksum;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Optional<String> optLine() {
        return Optional.empty();
    }

    public MetaData metaData() {
        return metaData;
    }

    public Optional<AccountingPlan> optAccountingPlan() {
        return Optional.ofNullable(accountingPlan);
    }

    public List<Voucher> vouchers() {
        return vouchers.stream().sorted().collect(Collectors.toList());
    }

    public List<Voucher> imbalancedVouchers() {
        return vouchers.stream().filter(voucher -> !voucher.balanced()).collect(Collectors.toList());
    }

    public Boolean isBalanced() {
        return imbalancedVouchers().isEmpty();
    }

    public List<AccountingDimension> dimensions() {
        return new ArrayList<>(dimensions);
    }

    public List<AccountingObject> objects() {
        return new ArrayList<>(objects);
    }

    public List<AccountingObject> costCentres() {
        return objects().stream().filter(obj -> obj.dimensionId().equals(AccountingDimension.COST_CENTRE)).collect(Collectors.toList());
    }

    public List<AccountingObject> costBearers() {
        return objects().stream().filter(obj -> obj.dimensionId().equals(AccountingDimension.COST_BEARER)).collect(Collectors.toList());
    }

    public List<AccountingObject> projects() {
        return objects().stream().filter(obj -> obj.dimensionId().equals(AccountingDimension.PROJECT)).collect(Collectors.toList());
    }

    public Optional<String> optChecksum() {
        return Optional.ofNullable(checksum);
    }

    @Override
    public String toString() {
        return "Document{"
                + "metaData=" + metaData + ", "
                + "accountingPlan=" + accountingPlan + ", "
                + "vouchers=" + vouchers + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.metaData);
        hash = 79 * hash + Objects.hashCode(this.accountingPlan);
        hash = 79 * hash + Objects.hashCode(this.vouchers);
        hash = 79 * hash + Objects.hashCode(this.dimensions);
        hash = 79 * hash + Objects.hashCode(this.objects);
        hash = 79 * hash + Objects.hashCode(this.checksum);
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
        final Document other = (Document) obj;
        if (!Objects.equals(this.checksum, other.checksum)) {
            return false;
        }
        if (!Objects.equals(this.metaData, other.metaData)) {
            return false;
        }
        if (!Objects.equals(this.accountingPlan, other.accountingPlan)) {
            return false;
        }
        if (!Objects.equals(this.vouchers, other.vouchers)) {
            return false;
        }
        if (!Objects.equals(this.dimensions, other.dimensions)) {
            return false;
        }
        return Objects.equals(this.objects, other.objects);
    }

    public static class Builder {

        private MetaData metaData;
        private AccountingPlan accountingPlan;
        private List<Voucher> vouchers = new ArrayList<>();
        private List<AccountingDimension> dimensions = new ArrayList<>();
        private List<AccountingObject> objects = new ArrayList<>();
        private String checksum;

        private Builder() {
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

        public Builder dimensions(List<AccountingDimension> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder objects(List<AccountingObject> objects) {
            this.objects = objects;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public Document apply() {
            if (checksum == null) {
                checksum = Sie4j.calculateChecksum(new Document(metaData, accountingPlan, vouchers, dimensions, objects, null));
            }
            return new Document(metaData, accountingPlan, vouchers, dimensions, objects, checksum);
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
        E1("Export av bokslutssaldon"),
        /**
         * Export of balances for periodical accounts.
         */
        E2("Export av periodsaldon"),
        /**
         * Export of balances for object accounts.
         */
        E3("Export av objektsaldon"),
        /**
         * Export of accounts, vouchers and transactions.
         * <p>
         * This type contains the most complete representation of the accounting
         * data.
         */
        E4("Export av transaktioner"),
        /**
         * Import of accounts, vouchers and transactions.
         * <p>
         * Accounts are optional for documents of this type.
         */
        I4("Import av transaktioner");

        public static final Type DEFAULT = E1;
        private final String description;

        private Type(String description) {
            this.description = description;
        }

        /**
         * Returns the description.
         * <p>
         * The description is given in Swedish.
         *
         * @return Description (in Swedish) of the purpose of the type.
         */
        public String getDescription() {
            return description;
        }

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
        public Integer getNumber() {
            return Integer.valueOf(name().replaceAll("\\D", ""));
        }

        public static Type get(String input) {
            try {
                return valueOf(input.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new InvalidSieTypeException(input, ex);
            } catch (NullPointerException ex) {
                throw new SieException("SIE type must not be null", ex);
            }
        }
    }
}
