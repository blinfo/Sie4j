package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.*;
import sie.io.LocalDateSerializer;

/**
 *
 * @author Håkan Lidén 
 *
 */
public final class FinancialYear implements Entity, Comparable<FinancialYear> {

    private final String line;
    private final Integer index;
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate startDate;
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate endDate;

    private FinancialYear(String line, Integer index, LocalDate startDate, LocalDate endDate) {
        this.line = line;
        this.index = index;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static FinancialYear of(Integer index, LocalDate startDate, LocalDate endDate) {
        return new FinancialYear(null, index, startDate, endDate);
    }

    public static FinancialYear of(String line, Integer index, LocalDate startDate, LocalDate endDate) {
        return new FinancialYear(line, index, startDate, endDate);
    }

    @Override
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public Integer index() {
        return index;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    @Override
    public int compareTo(FinancialYear other) {
        return other.index().compareTo(index());
    }

    @Override
    public String toString() {
        return "FinancialYear{" 
                + "index=" + index + ", "
                + "startDate=" + startDate + ", "
                + "endDate=" + endDate + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.index);
        hash = 73 * hash + Objects.hashCode(this.startDate);
        hash = 73 * hash + Objects.hashCode(this.endDate);
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
        final FinancialYear other = (FinancialYear) obj;
        if (!Objects.equals(this.index, other.index)) {
            return false;
        }
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        return Objects.equals(this.endDate, other.endDate);
    }
}
