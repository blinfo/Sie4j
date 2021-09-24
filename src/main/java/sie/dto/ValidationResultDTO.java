package sie.dto;

import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Account;
import sie.domain.AccountingDimension;
import sie.domain.AccountingObject;
import sie.domain.Company;
import sie.domain.FinancialYear;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
public class ValidationResultDTO implements DTO {

    private final Company company;
    private final List<FinancialYear> years;
    private final List<Voucher> vouchers;
    private final List<Account> accounts;
    private final List<AccountingDimension> dimensions;
    private final List<AccountingObject> objects;
    private final List<SieLogDTO> logs;

    private ValidationResultDTO(Company company, List<FinancialYear> years, List<Voucher> vouchers, List<Account> accounts, List<AccountingDimension> dimensions, List<AccountingObject> objects, List<SieLogDTO> logs) {
        this.company = company;
        this.years = years;
        this.vouchers = vouchers;
        this.accounts = accounts;
        this.dimensions = dimensions;
        this.objects = objects;
        this.logs = logs;
    }

    public static Builder builder() {
        return new Builder();
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

    public List<VoucherDTO> getVouchers() {
        return vouchers.stream().sorted().map(VoucherDTO::from).collect(Collectors.toList());
    }

    public List<SieLogDTO> getLogs() {
        return logs;
    }

    public static class Builder {

        private Company company;
        private List<FinancialYear> years;
        private List<Account> accounts;
        private List<AccountingObject> objects;
        private List<AccountingDimension> dimensions;
        private List<Voucher> vouchers;
        private List<SieLogDTO> logs;

        private Builder() {
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
        
        
        public Builder setLogs(List<SieLogDTO> logs) {
            this.logs = logs;
            return this;
        }

        public ValidationResultDTO build() {
            return new ValidationResultDTO(company, years, vouchers, accounts, dimensions, objects, logs);
        }
    }

}
