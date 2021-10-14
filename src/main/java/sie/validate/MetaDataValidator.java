package sie.validate;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import sie.domain.Document;
import sie.domain.FinancialYear;
import sie.domain.MetaData;
import sie.exception.NonConsecutiveFinancialYearsException;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
class MetaDataValidator extends AbstractValidator<MetaData> {

    private static final String FLAGG = "#FLAGGA",
            PROGRAM = "#PROGRAM",
            ADDRESS = "#ADRESS",
            COMPANY = "#FTAG",
            CIN = "#ORGNR",
            GENERATED = "#GEN",
            FINANCIAL_YEAR = "#RAR",
            PERIOD_RANGE = "#OMFATTN",
            CURRENCY = "#VALUTA";
    private static final Pattern CIN_PATTERN = Pattern.compile("\\d{6}-\\d{4}");
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("[A-Z]{3}");

    private MetaDataValidator(MetaData metaData) {
        super(metaData, metaData.getSieType());
    }

    static MetaDataValidator from(MetaData metaData) {
        return new MetaDataValidator(metaData);
    }

    @Override
    protected void validate() {
        if (entity.isRead() == null) {
            addInfo(FLAGG, "Flagga saknas");
        }
        if (entity.isRead()) {
            addWarning(FLAGG, "Filen markerad som tidigare inläst");
        }
        validateProgram();
        validateCompany();
        validateGenerated();
        validateFinancialYears();
        validatePeriodRange();
    }

    private void validateProgram() {
        if (entity.getProgram() == null) {
            addWarning(PROGRAM, "Programinformation saknas");
            return;
        }
        if (isNullOrBlank(entity.getProgram().getName())) {
            addWarning(PROGRAM, "Programnamn saknas");
        }
        if (isNullOrBlank(entity.getProgram().getVersion())) {
            addWarning(PROGRAM, "Programversion saknas");
        }
    }

    private void validateCompany() {
        if (entity.getCompany() == null) {
            addWarning(COMPANY, "Företagsinformation saknas");
            return;
        }
        if (isNullOrBlank(entity.getCompany().getName())) {
            addWarning(COMPANY, "Företagsnamn saknas");
        }
        entity.getCompany().getAddress().ifPresent(addr -> {
            if (addr.isEmpty()) {
                addWarning(ADDRESS, "Addressen är tom");
            } else {
                if (isNullOrBlank(addr.getContact())) {
                    addInfo(ADDRESS, "Kontaktperson saknas i adress");
                }
                if (isNullOrBlank(addr.getPhone())) {
                    addInfo(ADDRESS, "Telefonnummer saknas i adress");
                }
                if (isNullOrBlank(addr.getStreetAddress())) {
                    addInfo(ADDRESS, "Utdelningsadress saknas");
                }
                if (isNullOrBlank(addr.getPostalAddress())) {
                    addInfo(ADDRESS, "Postnummer och/eller ort saknas");
                }
            }
        });
        entity.getCompany().getCorporateID().ifPresent(cin -> {
            if (isNullOrBlank(cin)) {
                addWarning(CIN, "Organisationsnummer saknas");
            } else if (!CIN_PATTERN.matcher(cin).matches()) {
                addInfo(CIN, "Organisationsnummer ska vara av formatet nnnnnn-nnnn. " + cin);
            }
        });
    }

    private void validateGenerated() {
        if (entity.getGenerated() == null || entity.getGenerated().getDate() == null) {
            addWarning(GENERATED, "Uppgift om när filen skapades saknas");
        }
    }

    private void validateFinancialYears() {
        List<FinancialYear> years = entity.getFinancialYears();
        if (!type.equals(Document.Type.I4) && years.isEmpty()) {
            addWarning(FINANCIAL_YEAR, "Räkenskapsår saknas");
            return;
        }
        IntStream.range(0, years.size() - 1).forEach(i -> {
            LocalDate start = years.get(i).getStartDate();
            LocalDate end = years.get(i + 1).getEndDate();
            if (!start.equals(end.plusDays(1))) {
                SieException ex = new NonConsecutiveFinancialYearsException(years.get(i + 1));
                addCritical(getClass(), ex);
            }
        });
        years.forEach(year -> {
            if (year.getIndex() == null) {
                addWarning(FINANCIAL_YEAR, "Räkenskapsårets index (årsnummer) saknas");
            }
            if (year.getStartDate() == null) {
                addWarning(FINANCIAL_YEAR, "Räkenskapsårets startdatum saknas");
            }
            if (year.getEndDate() == null) {
                addWarning(FINANCIAL_YEAR, "Räkenskapsårets slutdatum saknas");
            }
        });
    }

    private void validatePeriodRange() {
        if (type.equals(Document.Type.E2) || type.equals(Document.Type.E3)) {
            if (!entity.getPeriodRange().isPresent()) {
                addWarning(PERIOD_RANGE, "Omfattning saknas");
            }
        }
    }

    private void validateCurrency() {
        entity.getCurrency().ifPresent(curr -> {
            if (!CURRENCY_PATTERN.matcher(curr).matches()) {
                addInfo(CURRENCY, "Valutakoden är felaktig");
            }
        });
    }
}
