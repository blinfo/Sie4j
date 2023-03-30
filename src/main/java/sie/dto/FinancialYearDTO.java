package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.time.LocalDate;
import sie.domain.FinancialYear;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"index", "startDate", "endDate"})
public record FinancialYearDTO(Integer index,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate startDate,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate endDate) implements DTO {

    public static FinancialYearDTO from(FinancialYear source) {
        return new FinancialYearDTO(source.getIndex(), source.getStartDate(), source.getEndDate());
    }
}
