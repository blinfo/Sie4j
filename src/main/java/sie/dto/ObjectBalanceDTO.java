package sie.dto;

import java.math.BigDecimal;
import sie.domain.Account.ObjectId;
import sie.domain.ObjectBalance;

/**
 *
 * @author Håkan Lidén
 */
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

    public ObjectId getObjectId() {
        return source.getObjectId();
    }

    public Double getQuantity() {
        return source.getQuantity().orElse(null);
    }
}
