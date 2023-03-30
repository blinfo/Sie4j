package sie.domain;

import java.util.*;

/**
 *
 * @author Håkan Lidén
 */
public final class AccountingDimension implements Entity, Comparable<AccountingDimension> {

    /**
     * Reserved dimension id for Cost centre
     */
    public static final Integer COST_CENTRE = 1;
    /**
     * Reserved dimension id for Cost bearer
     */
    public static final Integer COST_BEARER = 2;
    /**
     * Reserved dimension id for Project;
     */
    public static final Integer PROJECT = 6;
    /**
     * Reserved dimension id for Employee
     */
    public static final Integer EMPLOYEE = 7;
    /**
     * Reserved dimension id for Customer
     */
    public static final Integer CUSTOMER = 8;
    /**
     * Reserved dimension id for Supplier
     */
    public static final Integer SUPPLIER = 9;
    /**
     * Reserved dimension id for Invoice
     */
    public static final Integer INVOICE = 10;
    private final String line;
    private final Integer id;
    private final String label;
    private final Integer parentId;

    private AccountingDimension(String line, Integer id, String label, Integer parentId) {
        this.line = line;
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    public static AccountingDimension of(Integer id, String label, Integer parentId) {
        return of(null, id, label, parentId);
    }

    public static AccountingDimension of(String line, Integer id, String label, Integer parentId) {
        return new AccountingDimension(line, id, label, parentId);
    }

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public Integer id() {
        return id;
    }

    public String label() {
        return label;
    }

    public Boolean isSubDimension() {
        return optParentId().isPresent();
    }

    public Optional<Integer> optParentId() {
        return Optional.ofNullable(parentId);
    }

    @Override
    public int compareTo(AccountingDimension other) {
        return id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "AccountingDimension{" + "id=" + id + ", label=" + label + ", parentId=" + parentId + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.label);
        hash = 97 * hash + Objects.hashCode(this.parentId);
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
        final AccountingDimension other = (AccountingDimension) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.parentId, other.parentId);
    }
}
