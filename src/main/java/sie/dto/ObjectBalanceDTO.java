package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.ObjectBalance;
import sie.dto.AccountDTO.ObjectIdDTO;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "objectId", "amount", "quantity"})
public class ObjectBalanceDTO implements DTO {

    private Integer yearIndex;
    private ObjectIdDTO objectId;
    private BigDecimal amount;
    private Double quantity;

    public static ObjectBalanceDTO from(ObjectBalance source) {
        ObjectBalanceDTO dto = new ObjectBalanceDTO();
        dto.setYearIndex(source.getYearIndex());
        dto.setObjectId(ObjectIdDTO.from(source.getObjectId()));
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
