package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.AccountingObject;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"dimensionId", "number", "label"})
public class AccountingObjectDTO implements DTO {

    private Integer dimensionId;
    private String number;
    private String label;

    public AccountingObjectDTO() {
    }

    private AccountingObjectDTO(Integer dimensionId, String number, String label) {
        this.dimensionId = dimensionId;
        this.number = number;
        this.label = label;
    }

    public static AccountingObjectDTO from(AccountingObject source) {
        return new AccountingObjectDTO(source.getDimensionId(), source.getLabel(), source.getNumber());
    }

    public Integer getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(Integer dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
