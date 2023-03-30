package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.Balance;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "amount"})
public record BalanceDTO(Integer yearIndex, BigDecimal amount) implements DTO {

    public static BalanceDTO from(Balance source) {
        return new BalanceDTO(source.yearIndex(), source.amount());
    }
}
