package sie.domain;

import java.util.*;

/**
 *
 * @author Håkan Lidén
 */
public final class AccountingObject implements Entity, Comparable<AccountingObject> {

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
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public Integer dimensionId() {
        return dimensionId;
    }

    public String number() {
        return number;
    }

    public String label() {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.dimensionId);
        hash = 89 * hash + Objects.hashCode(this.number);
        hash = 89 * hash + Objects.hashCode(this.label);
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
        final AccountingObject other = (AccountingObject) obj;
        if (!Objects.equals(this.number, other.number)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return Objects.equals(this.dimensionId, other.dimensionId);
    }
}
