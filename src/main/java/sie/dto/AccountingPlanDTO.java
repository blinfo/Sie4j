package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.AccountingPlan;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"accounts", "type"})
public class AccountingPlanDTO implements DTO {

    private final AccountingPlan source;

    private AccountingPlanDTO(AccountingPlan source) {
        this.source = source;
    }

    public static AccountingPlanDTO from(AccountingPlan source) {
        return new AccountingPlanDTO(source);
    }

    public List<AccountDTO> getAccounts() {
        return source.getAccounts().stream().sorted().map(AccountDTO::from).collect(Collectors.toList());
    }

    public String getType() {
        return source.getType().orElse(null);
    }
}
