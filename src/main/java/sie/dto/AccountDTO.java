package sie.dto;

import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Account;
import sie.domain.Account.ObjectId;

/**
 *
 * @author Håkan Lidén
 */
public class AccountDTO implements DTO {

    private final Account source;

    private AccountDTO(Account source) {
        this.source = source;
    }

    public static AccountDTO from(Account source) {
        return new AccountDTO(source);
    }

    public String getNumber() {
        return source.getNumber();
    }

    public String getLabel() {
        return source.getLabel().orElse(null);
    }

    public String getUnit() {
        return source.getUnit().orElse(null);
    }

    public List<String> getSruCodes() {
        return source.getSruCodes();
    }

    public List<BalanceDTO> getOpeningBalances() {
        return source.getOpeningBalances().stream().map(BalanceDTO::from).collect(Collectors.toList());
    }

    public List<BalanceDTO> getClosingBalances() {
        return source.getClosingBalances().stream().map(BalanceDTO::from).collect(Collectors.toList());
    }

    public List<BalanceDTO> getResults() {
        return source.getResults().stream().map(BalanceDTO::from).collect(Collectors.toList());
    }

    public List<ObjectBalanceDTO> getObjectOpeningBalances() {
        return source.getObjectOpeningBalances().stream().map(ObjectBalanceDTO::from).collect(Collectors.toList());
    }

    public List<ObjectBalanceDTO> getObjectClosingBalances() {
        return source.getObjectClosingBalances().stream().map(ObjectBalanceDTO::from).collect(Collectors.toList());
    }
    
    public List<PeriodicalBalanceDTO> getPeriodicalBalances() {
        return source.getPeriodicalBalances().stream().map(PeriodicalBalanceDTO::from).collect(Collectors.toList());
    }
    
    public List<PeriodicalBudgetDTO> getPeriodicalBudgets() {
        return source.getPeriodicalBudgets().stream().map(PeriodicalBudgetDTO::from).collect(Collectors.toList());
    }

    public static class ObjectIdDTO implements DTO {

        private final ObjectId source;

        private ObjectIdDTO(ObjectId source) {
            this.source = source;
        }

        public static ObjectIdDTO from(ObjectId source) {
            return new ObjectIdDTO(source);
        }

        public Integer getDimensionId() {
            return source.getDimensionId();
        }

        public String getObjectNumber() {
            return source.getObjectNumber();
        }

    }
}
