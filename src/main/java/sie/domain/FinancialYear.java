package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.Optional;
import sie.io.LocalDateSerializer;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class FinancialYear implements Entity, Comparable<FinancialYear> {

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
    public Optional<String> getLine() {
        return Optional.ofNullable(line);
    }

    public Integer getIndex() {
        return index;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public int compareTo(FinancialYear other) {
        return other.getIndex().compareTo(getIndex());
    }

    @Override
    public String toString() {
        return "FinancialYear{" 
                + "index=" + index + ", "
                + "startDate=" + startDate + ", "
                + "endDate=" + endDate + '}';
    }
}
