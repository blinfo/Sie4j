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

    private List<AccountDTO> accounts;
    private String type;

    public AccountingPlanDTO() {
    }

    private AccountingPlanDTO(List<AccountDTO> accounts, String type) {
        this.accounts = accounts;
        this.type = type;
    }

    public static AccountingPlanDTO from(AccountingPlan source) {
        List<AccountDTO> accounts = source.getAccounts().stream().map(AccountDTO::from).collect(Collectors.toList());
        return new AccountingPlanDTO(accounts, source.getType().orElse(null));
    }

    public List<AccountDTO> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDTO> accounts) {
        this.accounts = accounts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
