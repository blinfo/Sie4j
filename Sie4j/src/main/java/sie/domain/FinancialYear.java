package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sie.io.JsonDateSerializer;
import java.time.LocalDate;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
public class FinancialYear implements Entity, Comparable<FinancialYear> {

    private final Integer index;
    @JsonSerialize(using = JsonDateSerializer.class)
    private final LocalDate startDate;
    @JsonSerialize(using = JsonDateSerializer.class)
    private final LocalDate endDate;

    private FinancialYear(Integer index, LocalDate startDate, LocalDate endDate) {
        this.index = index;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static FinancialYear of(Integer index, LocalDate startDate, LocalDate endDate) {
        return new FinancialYear(index, startDate, endDate);
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
