package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.ObjectBalance;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "objectId", "amount", "quantity"})
public class ObjectBalanceDTO implements DTO {

    private final ObjectBalance source;

    private ObjectBalanceDTO(ObjectBalance source) {
        this.source = source;
    }

    public static ObjectBalanceDTO from(ObjectBalance source) {
        return new ObjectBalanceDTO(source);
    }

    public BigDecimal getAmount() {
        return source.getAmount();
    }

    public Integer getYearIndex() {
        return source.getYearIndex();
    }

    public AccountDTO.ObjectIdDTO getObjectId() {
        if (source.getObjectId() == null) {
            return null;
        }
        return AccountDTO.ObjectIdDTO.from(source.getObjectId());
    }

    public Double getQuantity() {
        return source.getQuantity().orElse(null);
    }
}
