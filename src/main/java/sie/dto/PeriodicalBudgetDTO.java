package sie.dto;

import sie.domain.PeriodicalBudget;

/**
 *
 * @author Håkan Lidén
 */
public class PeriodicalBudgetDTO implements DTO {

    private final PeriodicalBudget source;

    private PeriodicalBudgetDTO(PeriodicalBudget source) {
        this.source = source;
    }

    public static PeriodicalBudgetDTO from(PeriodicalBudget source) {
        return new PeriodicalBudgetDTO(source);
    }
}
