package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Account;
import sie.domain.Account.ObjectId;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"number", "label", "unit", "sruCodes", "openingBalances",
    "closingBalances", "results", "objectOpeningBalances", "objectClosingBalances",
    "periodicalBalances", "periodicalBudgets"})
public class AccountDTO implements DTO {

    private String number;
    private String label;
    private String unit;
    private List<String> sruCodes;
    private List<BalanceDTO> openingBalances;
    private List<BalanceDTO> closingBalances;
    private List<BalanceDTO> results;
    private List<ObjectBalanceDTO> objectOpeningBalances;
    private List<ObjectBalanceDTO> objectClosingBalances;
    private List<PeriodicalBalanceDTO> periodicalBalances;
    private List<PeriodicalBudgetDTO> periodicalBudgets;


    public static AccountDTO from(Account source) {
        System.out.println(source);
        AccountDTO dto = new AccountDTO();
        dto.setNumber(source.getNumber());
        source.getLabel().ifPresent(dto::setLabel);
        source.getUnit().ifPresent(dto::setUnit);
        dto.setSruCodes(source.getSruCodes());
        dto.setOpeningBalances(source.getOpeningBalances().stream().map(BalanceDTO::from).collect(Collectors.toList()));
        dto.setClosingBalances(source.getClosingBalances().stream().map(BalanceDTO::from).collect(Collectors.toList()));
        dto.setResults(source.getResults().stream().map(BalanceDTO::from).collect(Collectors.toList()));
        dto.setObjectOpeningBalances(source.getObjectOpeningBalances().stream().map(ObjectBalanceDTO::from).collect(Collectors.toList()));
        dto.setObjectClosingBalances(source.getObjectClosingBalances().stream().map(ObjectBalanceDTO::from).collect(Collectors.toList()));
        dto.setPeriodicalBalances(source.getPeriodicalBalances().stream().map(PeriodicalBalanceDTO::from).collect(Collectors.toList()));
        dto.setPeriodicalBudgets(source.getPeriodicalBudgets().stream().map(PeriodicalBudgetDTO::from).collect(Collectors.toList()));
        return dto;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getSruCodes() {
        return sruCodes;
    }

    public void setSruCodes(List<String> sruCodes) {
        this.sruCodes = sruCodes;
    }

    public List<BalanceDTO> getOpeningBalances() {
        return openingBalances;
    }

    public void setOpeningBalances(List<BalanceDTO> openingBalances) {
        this.openingBalances = openingBalances;
    }

    public List<BalanceDTO> getClosingBalances() {
        return closingBalances;
    }

    public void setClosingBalances(List<BalanceDTO> closingBalances) {
        this.closingBalances = closingBalances;
    }

    public List<BalanceDTO> getResults() {
        return results;
    }

    public void setResults(List<BalanceDTO> results) {
        this.results = results;
    }

    public List<ObjectBalanceDTO> getObjectOpeningBalances() {
        return objectOpeningBalances;
    }

    public void setObjectOpeningBalances(List<ObjectBalanceDTO> objectOpeningBalances) {
        this.objectOpeningBalances = objectOpeningBalances;
    }

    public List<ObjectBalanceDTO> getObjectClosingBalances() {
        return objectClosingBalances;
    }

    public void setObjectClosingBalances(List<ObjectBalanceDTO> objectClosingBalances) {
        this.objectClosingBalances = objectClosingBalances;
    }

    public List<PeriodicalBalanceDTO> getPeriodicalBalances() {
        return periodicalBalances;
    }

    public void setPeriodicalBalances(List<PeriodicalBalanceDTO> periodicalBalances) {
        this.periodicalBalances = periodicalBalances;
    }

    public List<PeriodicalBudgetDTO> getPeriodicalBudgets() {
        return periodicalBudgets;
    }

    public void setPeriodicalBudgets(List<PeriodicalBudgetDTO> periodicalBudgets) {
        this.periodicalBudgets = periodicalBudgets;
    }

    @JsonPropertyOrder({"dimensionId", "objectNumber"})
    public static class ObjectIdDTO implements DTO {

        private Integer dimensionId;
        private String objectNumber;

        public ObjectIdDTO() {
        }

        private ObjectIdDTO(Integer dimensionId, String objectNumber) {
            this.dimensionId = dimensionId;
            this.objectNumber = objectNumber;
        }

        public static ObjectIdDTO from(ObjectId source) {
            return new ObjectIdDTO(source.getDimensionId(), source.getObjectNumber());
        }

        public Integer getDimensionId() {
            return dimensionId;
        }

        public void setDimensionId(Integer dimensionId) {
            this.dimensionId = dimensionId;
        }

        public String getObjectNumber() {
            return objectNumber;
        }

        public void setObjectNumber(String objectNumber) {
            this.objectNumber = objectNumber;
        }

    }
}
