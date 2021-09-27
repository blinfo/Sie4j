package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.AccountingDimension;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"id", "label", "parentId"})
public class AccountingDimensionDTO {

    private Integer id;
    private String label;
    private Integer parentId;

    public AccountingDimensionDTO() {
    }

    private AccountingDimensionDTO(Integer id, String label, Integer parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    public static AccountingDimensionDTO from(AccountingDimension source) {
        return new AccountingDimensionDTO(source.getId(), source.getLabel(), source.getParentId().orElse(null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}