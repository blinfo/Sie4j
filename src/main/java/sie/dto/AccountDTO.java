package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import sie.domain.Account;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"number", "label", "unit", "sruCodes", "openingBalances",
    "closingBalances", "results", "objectOpeningBalances", "objectClosingBalances",
    "periodicalBalances", "periodicalBudgets"})
public record AccountDTO(
        String number,
        String label,
        String unit,
        List<String> sruCodes,
        List<BalanceDTO> openingBalances,
        List<BalanceDTO> closingBalances,
        List<BalanceDTO> results,
        List<ObjectBalanceDTO> objectOpeningBalances,
        List<ObjectBalanceDTO> objectClosingBalances,
        List<PeriodicalBalanceDTO> periodicalBalances,
        List<PeriodicalBudgetDTO> periodicalBudgets) implements DTO {

    public static AccountDTO from(Account source) {
        return new AccountDTO(source.getNumber(),
                source.getLabel().orElse(null),
                source.getUnit().orElse(null),
                source.getSruCodes(),
                source.getOpeningBalances().stream().map(BalanceDTO::from).toList(),
                source.getClosingBalances().stream().map(BalanceDTO::from).toList(),
                source.getResults().stream().map(BalanceDTO::from).toList(),
                source.getObjectOpeningBalances().stream().map(ObjectBalanceDTO::from).toList(),
                source.getObjectClosingBalances().stream().map(ObjectBalanceDTO::from).toList(),
                source.getPeriodicalBalances().stream().map(PeriodicalBalanceDTO::from).toList(),
                source.getPeriodicalBudgets().stream().map(PeriodicalBudgetDTO::from).toList()
        );
    }

    @JsonPropertyOrder({"dimensionId", "objectNumber"})
    public static record ObjectIdDTO(Integer dimensionId, String objectNumber) implements DTO {

        public static ObjectIdDTO from(Account.ObjectId source) {
            return new ObjectIdDTO(source.getDimensionId(), source.getObjectNumber());
        }
    }
}
