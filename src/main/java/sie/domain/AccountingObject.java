package sie.domain;

import java.util.Optional;

/**
 *
 * @author Håkan Lidén
 */
public class AccountingObject implements Entity, Comparable<AccountingObject> {

    private final String line;
    private final Integer dimensionId;
    private final String number;
    private final String label;

    private AccountingObject(String line, Integer dimensionId, String number, String label) {
        this.line = line;
        this.dimensionId = dimensionId;
        this.number = number;
        this.label = label;
    }

    public static AccountingObject of(Integer dimensionId, String number, String label) {
        return of(null, dimensionId, number, label);
    }

    public static AccountingObject of(String line, Integer dimensionId, String number, String label) {
        return new AccountingObject(line, dimensionId, number, label);
    }

    @Override
    public Optional<String> getLine() {
        return Optional.ofNullable(line);
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

    @Override
    public int compareTo(AccountingObject other) {
        int result = dimensionId.compareTo(other.dimensionId);
        if (result == 0) {
            if (number.matches("\\d+") && other.number.matches("\\d+")) {
                return Integer.valueOf(number).compareTo(Integer.valueOf(other.number));
            }
            return number.compareTo(other.number);
        }
        return result;
    }
}
