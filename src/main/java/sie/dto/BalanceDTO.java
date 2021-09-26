package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.Balance;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "amount"})
public class BalanceDTO implements DTO {

    private final Balance source;

    private BalanceDTO(Balance source) {
        this.source = source;
    }

    public static BalanceDTO from(Balance source) {
        return new BalanceDTO(source);
    }

    public Integer getYearIndex() {
        return source.getYearIndex();
    }

    public BigDecimal getAmount() {
        return source.getAmount();
    }
}
