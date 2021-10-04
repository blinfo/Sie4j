package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sie.io.LocalDateSerializer;
import sie.io.YearSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public class MetaData implements Entity {

    private static final Document.Type DEFAULT_TYPE = Document.Type.E1;
    private final Boolean read;
    private final Program program;
    private final Generated generated;
    private final Document.Type sieType;
    private final String comments;
    private final Company company;
    @JsonSerialize(using = YearSerializer.class)
    private final Year taxationYear;
    private final List<FinancialYear> financialYears;
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate periodRange;
    private final String currency;

    private MetaData(Boolean read, Program program, Generated generated,
            Document.Type sieType, String comments, Company company, Year taxationYear,
            List<FinancialYear> financialYears, LocalDate periodRange, String currency) {
        this.read = read;
        this.program = program;
        this.generated = generated;
        this.sieType = sieType;
        this.comments = comments;
        this.company = company;
        this.taxationYear = taxationYear;
        this.financialYears = financialYears;
        this.periodRange = periodRange;
        this.currency = currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Shows if the file has been read.
     * <p>
     * The SIE standard requires this field to be set to false when the file is
     * created. The importing program should then alter the value to true to
     * avoid importing the same data multiple times.
     *
     *
     * @return Boolean - whether the file has been read or not.
     */
    public Boolean isRead() {
        return read;
    }

    /**
     * Identifier for the program.
     * <p>
     * The SIE standard requires identification of the exporting program.
     *
     * @return Program - identification of the exporting program
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Identifier for the exporting entity.
     * <p>
     * The SIE standard requires identification of the exporting entity.
     *
     * @return Generated - identification of the exporting entity.
     */
    public Generated getGenerated() {
        return generated;
    }

    public Document.Type getSieType() {
        return Optional.of(sieType).orElse(DEFAULT_TYPE);
    }

    public Optional<String> getComments() {
        return Optional.ofNullable(comments);
    }

    public Company getCompany() {
        return company;
    }

    public Optional<Year> getTaxationYear() {
        return Optional.ofNullable(taxationYear);
    }

    public List<FinancialYear> getFinancialYears() {
        return financialYears.stream().sorted().collect(Collectors.toList());
    }

    public Optional<FinancialYear> getFinancialYearByIndex(Integer index) {
        return financialYears.stream().filter(fy -> fy.getIndex().equals(index)).findFirst();
    }

    public Optional<LocalDate> getPeriodRange() {
        return Optional.ofNullable(periodRange);
    }

    public Optional<String> getCurrency() {
        return Optional.ofNullable(currency);
    }

    @Override
    public String toString() {
        return "MetaData{"
                + "read=" + read + ", "
                + "program=" + program + ", "
                + "generated=" + generated + ", "
                + "sieType=" + sieType + ", "
                + "comments=" + comments + ", "
                + "company=" + company + ", "
                + "taxationYear=" + taxationYear + ", "
                + "financialYears=" + financialYears + ", "
                + "periodRange=" + periodRange + ", "
                + "currency=" + currency + '}';
    }

    public static class Builder {

        private Boolean read = Boolean.FALSE;
        private Program program;
        private Generated generated;
        private Document.Type sieType;
        private String comments;
        private Company company;
        private Year taxationYear;
        private final List<FinancialYear> financialYears = new ArrayList<>();
        private LocalDate periodRange;
        private String currency;

        private Builder() {
        }

        /**
         * Required<sup>*</sup> - Indicator for the read state of the data.
         * <p>
         * <sup>*</sup> Required by the standard. If not set it will default to
         * Boolean.FALSE.
         * <p>
         * The SIE standard requires this field to be set to false when the data
         * is created. The importing program should then alter the value to true
         * to avoid importing the same data multiple times.
         *
         * @param read Boolean - whether the file has been read or not.
         * @return MetaData.Builder
         */
        public Builder read(Boolean read) {
            this.read = read;
            return this;
        }

        /**
         * Required - Identifier for the program.
         * <p>
         * The SIE standard requires identification of the exporting program.
         *
         * @param program Program
         * @return MetaData.Builder
         */
        public Builder program(Program program) {
            this.program = program;
            return this;
        }

        /**
         * Required - Identifier for the exporting entity.
         * <p>
         * The SIE standard requires identification of the exporting entity.
         *
         * @param generated Generated
         * @return MetaData.Builder
         */
        public Builder generated(Generated generated) {
            this.generated = generated;
            return this;
        }

        /**
         * Required - Version and type of the SIE format.
         * <p>
         * Which of the five formats for SIE is used. The Types are:
         * <ul>
         * <li>E1 - Export of balances for annual accounts
         * <li>E2 - Export of balances for periodical accounts
         * <li>E3 - Export of balances for object accounts
         * <li>E4 - Export of accounts, vouchers and transactions
         * <li>I4 - Import of accounts, vouchers and transactions
         * </ul>
         * <p>
         * If the type is omitted it is assumed that the type in question is E1.
         * <p>
         * Default is Document.Type.E1
         *
         * @param sieType Document.Type
         * @return MetaData.Builder
         */
        public Builder sieType(Document.Type sieType) {
            this.sieType = sieType;
            return this;
        }

        /**
         * Optional - Text field for optional information.
         *
         * @param comments String
         * @return MetaData.Builder
         */
        public Builder comments(String comments) {
            this.comments = comments;
            return this;
        }

        /**
         * Required - Identification of the Company owning the exported data.
         * <p>
         * At least the name of the company in question is required.
         *
         * @param company Company
         * @return MetaData.Builder
         */
        public Builder company(Company company) {
            this.company = company;
            return this;
        }

        /**
         * Optional - The taxation year.
         * <p>
         * As of 2013-09-13 this field is obsolete due to changes in the Swedish
         * taxation laws for companies. The field is kept for backwards
         * compatibility.
         *
         * @param taxationYear Year
         * @return MetaData.Builder
         */
        public Builder taxationYear(Year taxationYear) {
            this.taxationYear = taxationYear;
            return this;
        }

        /**
         * Required<sup>*</sup> - A list of the financial years included.
         * <p>
         * <sup>*</sup> This is required for all SIE types except for type I4.
         *
         * @param financialYears List of FinancialYears
         * @return MetaData.Builder
         */
        public Builder financialYears(List<FinancialYear> financialYears) {
            this.financialYears.addAll(financialYears);
            return this;
        }

        /**
         * The latest date included in the export.
         * <p>
         * This field is required for types E2 and E3, optional for type E4 and
         * forbidden in types E1 and I4.
         *
         * @param periodRange LocalDate
         * @return MetaData.Builder
         */
        public Builder periodRange(LocalDate periodRange) {
            this.periodRange = periodRange;
            return this;
        }

        /**
         * Optional - Identifies the currency used.
         * <p>
         * If no currency is provided it is assumed that SEK (Swedish Crown) is
         * used.
         *
         * @param currency String
         * @return MetaData.Builder
         */
        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * Build the MetaData.
         *
         * @return MetaData representing the data in the builder.
         */
        public MetaData apply() {
            return new MetaData(read, program, generated, sieType, comments,
                    company, taxationYear, financialYears, periodRange, currency);
        }
    }
}
