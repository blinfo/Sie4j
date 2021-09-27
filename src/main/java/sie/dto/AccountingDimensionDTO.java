package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.AccountingDimension;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"id", "label", "parentId"})
public class AccountingDimensionDTO {

    private final AccountingDimension source;

    private AccountingDimensionDTO(AccountingDimension source) {
        this.source = source;
    }

    public static AccountingDimensionDTO from(AccountingDimension source) {
        return new AccountingDimensionDTO(source);
    }
    
    public Integer getId() {
        return source.getId();
    }
    
    public String getLabel() {
        return source.getLabel();
    }
    
    public Integer getParentId() {
        return source.getParentId().orElse(null);
    }

}
