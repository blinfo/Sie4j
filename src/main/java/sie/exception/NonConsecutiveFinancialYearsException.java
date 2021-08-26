package sie.exception;

import sie.domain.Entity;
import sie.domain.FinancialYear;

/**
 *
 * @author Håkan Lidén
 */
public class NonConsecutiveFinancialYearsException extends SieException {

    public NonConsecutiveFinancialYearsException(FinancialYear year) {
        super("Slutdatum för år " + year.getIndex() + " är inte direkt före nästa års startdatum", Entity.FINANCIAL_YEAR);
    }

}
