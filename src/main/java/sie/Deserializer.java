package sie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Account;
import sie.domain.AccountingDimension;
import sie.domain.AccountingObject;
import sie.domain.AccountingPlan;
import sie.domain.Balance;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.FinancialYear;
import sie.domain.Generated;
import sie.domain.MetaData;
import sie.domain.ObjectBalance;
import sie.domain.Program;
import sie.domain.Transaction;
import sie.domain.Voucher;
import sie.dto.AccountDTO;
import sie.dto.AccountingDimensionDTO;
import sie.dto.AccountingObjectDTO;
import sie.dto.BalanceDTO;
import sie.dto.CompanyDTO;
import sie.dto.DocumentDTO;
import sie.dto.FinancialYearDTO;
import sie.dto.ObjectBalanceDTO;
import sie.dto.TransactionDTO;
import sie.dto.VoucherDTO;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
class Deserializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Document fromJson(DocumentDTO dto) {
        return new Deserializer().parse(dto);
    }

    public static Document fromJson(String jsonString) {
        try {
            return new Deserializer().parse(MAPPER.readValue(jsonString, DocumentDTO.class));
        } catch (JsonProcessingException ex) {
            throw new SieException("Malformed jsonString", ex);
        }
    }

    public Document parse(DocumentDTO dto) {
        return Document.builder()
                .metaData(getMetaData(dto))
                .dimensions(getDimensions(dto))
                .objects(getObjects(dto))
                .accountingPlan(getAccountingPlan(dto))
                .vouchers(getVouchers(dto)).apply();
    }

    private MetaData getMetaData(DocumentDTO dto) {
        MetaData.Builder builder = MetaData.builder()
                .generated(Generated.from(LocalDate.now())) // Ska det importeras?
                .financialYears(dto.getYears().stream().map(this::createFinancialYear).collect(Collectors.toList()))
                .read(Boolean.FALSE)
                .company(createCompany(dto.getCompany()))
                .program(Program.of("BL APP", "1.0"));
        if (dto.getType() != null) {
            builder.sieType(Document.Type.get(dto.getType().getType()));
        }
        return builder.apply();
    }

    private List<AccountingDimension> getDimensions(DocumentDTO dto) {
        return dto.getDimensions().stream()
                .map(this::createAccountingDimension)
                .collect(Collectors.toList());
    }

    private List<AccountingObject> getObjects(DocumentDTO dto) {
        return dto.getObjects().stream()
                .map(this::createAccountingObject)
                .collect(Collectors.toList());
    }

    private AccountingPlan getAccountingPlan(DocumentDTO dto) {
        return AccountingPlan.builder()
                .accounts(dto.getAccounts().stream().map(this::createAccount).collect(Collectors.toList())).apply();
    }

    private List<Voucher> getVouchers(DocumentDTO dto) {
        return dto.getVouchers().stream()
                .map(this::createVoucher)
                .collect(Collectors.toList());
    }

    private FinancialYear createFinancialYear(FinancialYearDTO input) {
        return FinancialYear.of(input.getIndex(), LocalDate.parse(input.getStartDate()), LocalDate.parse(input.getEndDate()));
    }

    private Company createCompany(CompanyDTO company) {
        return Company.builder(company.getName())
                .corporateID(company.getCorporateID())
                .apply();
    }

    private AccountingDimension createAccountingDimension(AccountingDimensionDTO input) {
        return AccountingDimension.of(input.getId(), input.getLabel(), input.getParentId());
    }

    private AccountingObject createAccountingObject(AccountingObjectDTO input) {
        return AccountingObject.of(input.getDimensionId(), input.getNumber(), input.getLabel());
    }

    private Account createAccount(AccountDTO input) {
        Account.Builder builder = Account.builder(input.getNumber())
                .label(input.getLabel())
                .unit(input.getUnit());
        input.getOpeningBalances().stream().map(this::createBalance).forEach(builder::addOpeningBalance);
        input.getClosingBalances().stream().map(this::createBalance).forEach(builder::addClosingBalance);
        input.getResults().stream().map(this::createBalance).forEach(builder::addResult);
        input.getObjectOpeningBalances().stream().map(this::createObjectBalance).forEach(builder::addObjectOpeningBalance);
        input.getObjectClosingBalances().stream().map(this::createObjectBalance).forEach(builder::addObjectClosingBalance);
        return builder.apply();
    }

    private Balance createBalance(BalanceDTO input) {
        return Balance.of(input.getAmount(), input.getYearIndex());
    }

    private ObjectBalance createObjectBalance(ObjectBalanceDTO input) {
        return ObjectBalance.builder()
                .amount(input.getAmount())
                .yearIndex(input.getYearIndex())
                .objectId(input.getObjectId().getDimensionId(), input.getObjectId().getObjectNumber())
                .quantity(input.getQuantity())
                .apply();
    }

    private Voucher createVoucher(VoucherDTO input) {
        Voucher.Builder builder = Voucher.builder()
                .date(LocalDate.parse(input.getDate()))
                .number(input.getNumber())
                .series(input.getSeries())
                .signature(input.getSignature())
                .text(input.getText());
        if (input.getRegistrationDate() != null) {
            builder.registrationDate(LocalDate.parse(input.getRegistrationDate()));
        }
        input.getTransactions().stream().map(this::createTransaction).forEach(builder::addTransaction);
        return builder.apply();
    }

    private Transaction createTransaction(TransactionDTO input) {
        Transaction.Builder builder = Transaction.builder()
                .accountNumber(input.getAccountNumber())
                .amount(input.getAmount())
                .quantity(input.getQuantity())
                .signature(input.getSignature())
                .text(input.getText());
        if (input.getDate() != null) {
            builder.date(LocalDate.parse(input.getDate()));
        }
        input.getCostCentreIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.COST_CENTRE, num)).forEach(builder::addObjectId);
        input.getCostBearerIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.COST_BEARER, num)).forEach(builder::addObjectId);
        input.getProjectIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.PROJECT, num)).forEach(builder::addObjectId);
        return builder.apply();
    }
}
