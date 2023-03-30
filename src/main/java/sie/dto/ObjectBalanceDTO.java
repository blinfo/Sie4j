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
public record ObjectBalanceDTO(Integer yearIndex,
        ObjectIdDTO objectId,
        BigDecimal amount,
        Double quantity) implements DTO {

    public static ObjectBalanceDTO from(ObjectBalance source) {
        return new ObjectBalanceDTO(source.getYearIndex(),
                ObjectIdDTO.from(source.getObjectId()),
                source.getAmount(),
                source.getQuantity().orElse(null));
    }
}
