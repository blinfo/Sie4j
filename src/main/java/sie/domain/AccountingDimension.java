package sie.domain;

import java.util.Optional;

/**
 *
 * @author Håkan Lidén
 */
public class AccountingDimension implements Entity, Comparable<AccountingDimension> {

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
    private final Integer id;
    private final String label;
    private final Integer parentId;

    private AccountingDimension(Integer id, String label, Integer parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    public static AccountingDimension of(Integer id, String label, Integer parentId) {
        return new AccountingDimension(id, label, parentId);
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Boolean isSubDimension() {
        return getParentId().isPresent();
    }

    public Optional<Integer> getParentId() {
        return Optional.ofNullable(parentId);
    }

    @Override
    public String toString() {
        return "AccountingDimension{" + "id=" + id + ", label=" + label + ", parentId=" + parentId + '}';
    }

    @Override
    public int compareTo(AccountingDimension other) {
        return id.compareTo(other.id);
    }
}
