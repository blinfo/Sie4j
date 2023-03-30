package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import sie.domain.PeriodicalBalance;
import sie.dto.AccountDTO.ObjectIdDTO;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "period", "objectId", "amount", "quantity"})
public record PeriodicalBalanceDTO(Integer yearIndex,
        @JsonSerialize(using = YearMonthSerializer.class)
        @JsonDeserialize(using = YearMonthDeserializer.class)
        YearMonth period,
        ObjectIdDTO objectId,
        BigDecimal amount,
        Double quantity) implements DTO {

    public static PeriodicalBalanceDTO from(PeriodicalBalance source) {
        return new PeriodicalBalanceDTO(
                source.yearIndex(),
                source.period(),
                source.optObjectId().map(ObjectIdDTO::from).orElse(null),
                source.amount(),
                source.optQuantity().orElse(null));
    }
}
