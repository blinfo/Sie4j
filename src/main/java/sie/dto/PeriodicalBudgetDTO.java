package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import sie.domain.PeriodicalBudget;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "period", "amount"})
public record PeriodicalBudgetDTO(Integer yearIndex,
        @JsonSerialize(using = YearMonthSerializer.class)
        @JsonDeserialize(using = YearMonthDeserializer.class)
        YearMonth period,
        BigDecimal amount) implements DTO {

    public static PeriodicalBudgetDTO from(PeriodicalBudget source) {
        return new PeriodicalBudgetDTO(
                source.yearIndex(),
                source.period(),
                source.amount());
    }
}
