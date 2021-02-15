package sie.domain;

import java.util.Optional;

/**
 *
 * @author hakan
 */
public class AccountingDimension {

    private final Integer id;
    private final String label;
    private final Integer parentId;

    private AccountingDimension(Integer id, String label, Integer parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    public static AccountingDimension of(Integer id, String label) {
        return new AccountingDimension(id, label, null);
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

}
