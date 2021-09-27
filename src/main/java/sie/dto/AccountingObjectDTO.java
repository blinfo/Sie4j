package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.AccountingObject;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"dimensionId", "number", "label"})
public class AccountingObjectDTO implements DTO {

    private final AccountingObject source;

    private AccountingObjectDTO(AccountingObject source) {
        this.source = source;
    }

    public static AccountingObjectDTO from(AccountingObject source) {
        return new AccountingObjectDTO(source);
    }

    public Integer getDimensionId() {
        return source.getDimensionId();
    }

    public String getNumber() {
        if (source.getNumber().isBlank()) {
            return null;
        }
        return source.getNumber();
    }

    public String getLabel() {
        if (source.getLabel().isBlank()) {
            return null;
        }
        return source.getLabel();
    }
}
