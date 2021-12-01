package sie.exception;

import sie.domain.Entity;
import sie.domain.FinancialYear;

/**
 *
 * @author Håkan Lidén
 */
public class NonConsecutiveFinancialYearsException extends SieException {

    public static final String SUFFIX = " är inte direkt före nästa års startdatum";
    public static final String PREFIX = "Slutdatum för år ";

    public NonConsecutiveFinancialYearsException(FinancialYear year) {
        super(PREFIX + year.getIndex() + SUFFIX, Entity.FINANCIAL_YEAR);
    }

}
