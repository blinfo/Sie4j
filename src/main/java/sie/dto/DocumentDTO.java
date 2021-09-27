package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Account;
import sie.domain.AccountingDimension;
import sie.domain.AccountingObject;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.FinancialYear;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"type", "company", "years", "dimensions", "objects", "accounts", "voucherNumberSeries", "vouchers"})
public class DocumentDTO implements DTO {

    private final Document.Type type;
    private final Company company;
    private final List<FinancialYear> years;
    private final List<Voucher> vouchers;
    private final List<Account> accounts;
    private final List<AccountingDimension> dimensions;
    private final List<AccountingObject> objects;

    private DocumentDTO(Document.Type type,
            Company company,
            List<FinancialYear> years,
            List<Voucher> vouchers,
            List<Account> accounts,
            List<AccountingDimension> dimensions,
            List<AccountingObject> objects) {
        this.type = type;
        this.company = company;
        this.years = years;
        this.vouchers = vouchers;
        this.accounts = accounts;
        this.dimensions = dimensions;
        this.objects = objects;
    }

    public static DocumentDTO from(Document document) {
        Builder builder = DocumentDTO.builder()
                .setType(document.getMetaData().getSieType())
                .setCompany(document.getMetaData().getCompany())
                .setYears(document.getMetaData().getFinancialYears())
                .setDimensions(document.getDimensions())
                .setObjects(document.getObjects())
                .setVouchers(document.getVouchers());
        builder.setAccounts(document.getAccountingPlan().stream().flatMap(ap -> ap.getAccounts().stream()).collect(Collectors.toList()));
        return builder.apply();
    }

    public static Builder builder() {
        return new Builder();
    }

    public SieTypeDTO getType() {
        return type == null ? null : SieTypeDTO.from(type);
    }

    public CompanyDTO getCompany() {
        return company == null ? null : CompanyDTO.from(company);
    }

    public List<FinancialYearDTO> getYears() {
        return years.stream().map(FinancialYearDTO::from).collect(Collectors.toList());
    }

    public List<AccountDTO> getAccounts() {
        return accounts.stream().sorted().map(AccountDTO::from).collect(Collectors.toList());
    }

    public List<AccountingDimensionDTO> getDimensions() {
        return dimensions.stream().sorted().map(AccountingDimensionDTO::from).collect(Collectors.toList());
    }

    public List<AccountingObjectDTO> getObjects() {
        return objects.stream().sorted().map(AccountingObjectDTO::from).collect(Collectors.toList());
    }

    public List<String> getVoucherNumberSeries() {
        return vouchers.stream()
                .map(v -> v.getSeries().orElse(null))
                .filter(s -> s != null)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> getVouchers() {
        return vouchers.stream().sorted().map(VoucherDTO::from).collect(Collectors.toList());
    }

    public static class Builder {

        private Document.Type type;
        private Company company;
        private List<FinancialYear> years;
        private List<Account> accounts;
        private List<AccountingObject> objects;
        private List<AccountingDimension> dimensions;
        private List<Voucher> vouchers;

        private Builder() {
        }

        public Builder setType(Document.Type type) {
            this.type = type;
            return this;
        }

        public Builder setCompany(Company company) {
            this.company = company;
            return this;
        }

        public Builder setYears(List<FinancialYear> years) {
            this.years = years;
            return this;
        }

        public Builder setVouchers(List<Voucher> vouchers) {
            this.vouchers = vouchers;
            return this;
        }

        public Builder setAccounts(List<Account> accounts) {
            this.accounts = accounts;
            return this;
        }

        public Builder setDimensions(List<AccountingDimension> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder setObjects(List<AccountingObject> objects) {
            this.objects = objects;
            return this;
        }

        public DocumentDTO apply() {
            return new DocumentDTO(type, company, years, vouchers, accounts, dimensions, objects);
        }
    }

}
