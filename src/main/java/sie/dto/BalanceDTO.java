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

    private Integer yearIndex;
    private BigDecimal amount;

    public BalanceDTO() {
    }

    private BalanceDTO(Integer yearIndex, BigDecimal amount) {
        this.yearIndex = yearIndex;
        this.amount = amount;
    }

    public static BalanceDTO from(Balance source) {
        return new BalanceDTO(source.getYearIndex(), source.getAmount());
    }

    public Integer getYearIndex() {
        return yearIndex;
    }

    public void setYearIndex(Integer yearIndex) {
        this.yearIndex = yearIndex;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
