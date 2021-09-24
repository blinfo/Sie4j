package sie.dto;

import sie.domain.AccountingObject;

/**
 *
 * @author Håkan Lidén
 */
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
        return source.getNumber();
    }

    public String getLabel() {
        return source.getLabel();
    }
}
