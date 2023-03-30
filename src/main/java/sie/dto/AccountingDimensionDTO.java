package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.AccountingDimension;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"id", "label", "parentId"})
public record AccountingDimensionDTO(Integer id, String label, Integer parentId) implements DTO {

    public static AccountingDimensionDTO from(AccountingDimension source) {
        return new AccountingDimensionDTO(source.getId(), source.getLabel(), source.getParentId().orElse(null));
    }

}
