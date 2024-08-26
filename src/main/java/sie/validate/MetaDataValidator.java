package sie.validate;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import sie.domain.*;
import sie.exception.*;

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
        super(metaData, metaData.sieType());
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
        if (entity.program() == null) {
            addWarning(PROGRAM, "Programinformation saknas");
            return;
        }
        if (isNullOrBlank(entity.program().name())) {
            addWarning(PROGRAM, "Programnamn saknas" + entity.program().optLine().map(l -> "\n " + l).orElse(""));
        }
        if (isNullOrBlank(entity.program().version())) {
            addInfo(PROGRAM, "Programversion saknas" + entity.program().optLine().map(l -> "\n " + l).orElse(""));
        }
    }

    private void validateCompany() {
        if (entity.getCompany() == null) {
            addWarning(COMPANY, "Företagsinformation saknas");
            return;
        }
        if (isNullOrBlank(entity.getCompany().name())) {
            addWarning(COMPANY, "Företagsnamn saknas" + entity.getCompany().optLine().map(l -> "\n " + l).orElse(""));
        }
        entity.getCompany().optAddress().ifPresent(addr -> {
            if (addr.isEmpty()) {
                addWarning(ADDRESS, "Addressen är tom");
            } else {
                if (isNullOrBlank(addr.contact())) {
                    addInfo(ADDRESS, "Kontaktperson saknas i adress" + addr.optLine().map(l -> "\n " + l).orElse(""));
                }
                if (isNullOrBlank(addr.phone())) {
                    addInfo(ADDRESS, "Telefonnummer saknas i adress" + addr.optLine().map(l -> "\n " + l).orElse(""));
                }
                if (isNullOrBlank(addr.streetAddress())) {
                    addInfo(ADDRESS, "Utdelningsadress saknas" + addr.optLine().map(l -> "\n " + l).orElse(""));
                }
                if (isNullOrBlank(addr.postalAddress())) {
                    addInfo(ADDRESS, "Postnummer och/eller ort saknas" + addr.optLine().map(l -> "\n " + l).orElse(""));
                }
            }
        });
        entity.getCompany().optCorporateId().ifPresent(cin -> {
            if (isNullOrBlank(cin)) {
                addWarning(CIN, "Organisationsnummer saknas");
            } else if (!CIN_PATTERN.matcher(cin).matches()) {
                addInfo(CIN, "Organisationsnummer ska vara av formatet nnnnnn-nnnn. " + cin);
            }
        });
    }

    private void validateGenerated() {
        if (entity.generated() == null || entity.generated().date() == null) {
            addWarning(GENERATED, "Uppgift om när filen skapades saknas" + entity.generated().optLine().map(l -> "\n " + l).orElse(""));
        }
    }

    private void validateFinancialYears() {
        List<FinancialYear> years = entity.financialYears();
        if (!type.equals(Document.Type.I4) && years.isEmpty()) {
            addWarning(FINANCIAL_YEAR, "Räkenskapsår saknas");
            return;
        }
        IntStream.range(0, years.size() - 1).forEach(i -> {
            LocalDate start = years.get(i).startDate();
            LocalDate end = years.get(i + 1).endDate();
            if (!start.equals(end.plusDays(1))) {
                SieException ex = new NonConsecutiveFinancialYearsException(years.get(i + 1));
                addCritical(getClass(), ex);
            }
        });
        years.forEach(year -> {
            if (year.index() == null) {
                addWarning(FINANCIAL_YEAR, "Räkenskapsårets index (årsnummer) saknas" + year.optLine().map(l -> "\n " + l).orElse(""));
            }
            if (year.startDate() == null) {
                addWarning(FINANCIAL_YEAR, "Räkenskapsårets startdatum saknas" + year.optLine().map(l -> "\n " + l).orElse(""));
            }
            if (year.endDate() == null) {
                addWarning(FINANCIAL_YEAR, "Räkenskapsårets slutdatum saknas" + year.optLine().map(l -> "\n " + l).orElse(""));
            }
        });
    }

    private void validatePeriodRange() {
        if ((type.equals(Document.Type.E2) || type.equals(Document.Type.E3)) && !entity.optPeriodRange().isPresent()) {
            addWarning(PERIOD_RANGE, "Omfattning saknas");
        }
    }
}
