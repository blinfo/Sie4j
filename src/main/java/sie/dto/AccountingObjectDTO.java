package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.AccountingObject;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"dimensionId", "number", "label"})
public record AccountingObjectDTO(Integer dimensionId, String number, String label) implements DTO {

    public static AccountingObjectDTO from(AccountingObject source) {
        return new AccountingObjectDTO(source.dimensionId(), source.number(), source.label());
    }
}
