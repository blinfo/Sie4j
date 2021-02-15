package sie.domain;

/**
 *
 * @author Håkan Lidén
 */
public class AccountingObject {

    private final Integer dimensionId;
    private final String number;
    private final String label;

    private AccountingObject(Integer dimensionId, String number, String label) {
        this.dimensionId = dimensionId;
        this.number = number;
        this.label = label;
    }

    public static AccountingObject of(Integer dimensionId, String number, String label) {
        return new AccountingObject(dimensionId, number, label);
    }

    public Integer getDimensionId() {
        return dimensionId;
    }

    public String getNumber() {
        return number;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "AccountingObject{" + "dimensionId=" + dimensionId + ", number=" + number + ", label=" + label + '}';
    }
}
