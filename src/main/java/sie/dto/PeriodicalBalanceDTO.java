package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import sie.domain.Entity;
import sie.domain.PeriodicalBalance;
import sie.dto.AccountDTO.ObjectIdDTO;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"yearIndex", "yearMonth", "objectId", "amount", "quantity"})
public class PeriodicalBalanceDTO implements DTO {

    private final PeriodicalBalance source;

    private PeriodicalBalanceDTO(PeriodicalBalance source) {
        this.source = source;
    }

    public static PeriodicalBalanceDTO from(PeriodicalBalance source) {
        return new PeriodicalBalanceDTO(source);
    }

    public Integer getYearIndex() {
        return source.getYearIndex();
    }

    public String getYearMonth() {
        return source.getPeriod().format(Entity.YEAR_MONTH_FORMAT);
    }

    public ObjectIdDTO getObjectId() {
        return source.getObjectId().map(ObjectIdDTO::from).orElse(null);
    }

    public BigDecimal getAmount() {
        return source.getAmount();
    }

    public Double getQuantity() {
        return source.getQuantity().orElse(null);
    }

}
