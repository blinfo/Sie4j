package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import sie.domain.AccountingPlan;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"accounts", "type"})
public record AccountingPlanDTO(List<AccountDTO> accounts, String type) implements DTO {

    public static AccountingPlanDTO from(AccountingPlan source) {
        List<AccountDTO> accounts = source.accounts().stream().map(AccountDTO::from).toList();
        return new AccountingPlanDTO(accounts, source.optType().orElse(null));
    }
}
