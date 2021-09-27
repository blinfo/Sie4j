package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.PeriodicalBalance;
import sie.dto.AccountDTO.ObjectIdDTO;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "period", "objectId", "amount", "quantity"})
public class PeriodicalBalanceDTO implements DTO {

    private Integer yearIndex;
    private String period;
    private ObjectIdDTO objectId;
    private BigDecimal amount;
    private Double quantity;

    public static PeriodicalBalanceDTO from(PeriodicalBalance source) {
        PeriodicalBalanceDTO dto = new PeriodicalBalanceDTO();
        dto.setYearIndex(source.getYearIndex());
        dto.setPeriod(source.getPeriod().toString());
        source.getObjectId().map(ObjectIdDTO::from).ifPresent(dto::setObjectId);
        dto.setAmount(source.getAmount());
        source.getQuantity().ifPresent(dto::setQuantity);
        return dto;
    }

    public Integer getYearIndex() {
        return yearIndex;
    }

    public void setYearIndex(Integer yearIndex) {
        this.yearIndex = yearIndex;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public ObjectIdDTO getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectIdDTO objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
