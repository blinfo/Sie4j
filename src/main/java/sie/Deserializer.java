package sie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import sie.domain.*;
import sie.dto.*;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
class Deserializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Document fromJson(byte[] source) {
        return fromJson(bytesToString(source));
    }

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
                .metaData(getMetaData(dto.getMetaData()))
                .dimensions(getDimensions(dto))
                .objects(getObjects(dto))
                .accountingPlan(getAccountingPlan(dto))
                .vouchers(getVouchers(dto)).apply();
    }

    private MetaData getMetaData(MetaDataDTO dto) {
        return MetaData.builder()
                .company(createCompany(dto.getCompany()))
                .currency(dto.getCurrency())
                .financialYears(dto.getFinancialYears().stream().map(this::createFinancialYear).collect(Collectors.toList()))
                .generated(createGenerated(dto.getGenerated()))
                .periodRange(Optional.ofNullable(dto.getPeriodRange()).map(LocalDate::parse).orElse(null))
                .program(createProgram(dto.getProgram()))
                .read(dto.isRead())
                .sieType(Optional.ofNullable(dto.getSieType().getType()).map(Document.Type::get).orElse(null))
                .taxationYear(Optional.ofNullable(dto.getTaxationYear()).map(Year::parse).orElse(null))
                .apply();
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
        if (dto.getAccountingPlan() == null) {
            return null;
        }
        return AccountingPlan.builder()
                .accounts(dto.getAccountingPlan().getAccounts().stream().map(this::createAccount).collect(Collectors.toList()))
                .type(dto.getAccountingPlan().getType())
                .apply();
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
        if (company == null || isEmpty(company.getName()) && isEmpty(company.getCorporateId())) {
            return null;
        }
        return Company.builder(company.getName())
                .corporateID(company.getCorporateId())
                .address(createAddress(company.getAddress()))
                .aquisitionNumber(company.getAquisitionNumber())
                .sniCode(company.getSniCode())
                .id(company.getId())
                .type(Optional.ofNullable(company.getType()).map(t -> Company.Type.from(t.getType())).orElse(null))
                .apply();
    }

    private Generated createGenerated(GeneratedDTO generated) {
        if (generated == null || isEmpty(generated.getDate())) {
            return null;
        }
        return Generated.of(LocalDate.parse(generated.getDate()), generated.getSignature());
    }

    private Program createProgram(ProgramDTO program) {
        if (program == null || isEmpty(program.getName())) {
            return Program.of("BL APP", "1.0");
        }
        return Program.of(program.getName(), program.getVersion());
    }

    private Address createAddress(AddressDTO address) {
        if (address == null) {
            return null;
        }
        Address.Builder builder = Address.builder();
        builder.contact(address.getContact())
                .streetAddress(address.getStreetAddress())
                .postalAddress(address.getPostalAddress())
                .phone(address.getPhone());
        if (!builder.isEmpty()) {
            return builder.apply();
        }
        return null;
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
        input.getSruCodes().forEach(builder::addSruCode);
        input.getOpeningBalances().stream().map(this::createBalance).forEach(builder::addOpeningBalance);
        input.getClosingBalances().stream().map(this::createBalance).forEach(builder::addClosingBalance);
        input.getResults().stream().map(this::createBalance).forEach(builder::addResult);
        input.getObjectOpeningBalances().stream().map(this::createObjectBalance).forEach(builder::addObjectOpeningBalance);
        input.getObjectClosingBalances().stream().map(this::createObjectBalance).forEach(builder::addObjectClosingBalance);
        input.getPeriodicalBalances().stream().map(this::createPeriodicalBalance).forEach(builder::addPeriodicalBalance);
        input.getPeriodicalBudgets().stream().map(this::createPeriodicalBudget).forEach(builder::addPeriodicalBudget);
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

    private PeriodicalBalance createPeriodicalBalance(PeriodicalBalanceDTO input) {
        PeriodicalBalance.Builder builder = PeriodicalBalance.builder()
                .amount(input.getAmount())
                .period(YearMonth.parse(input.getPeriod()))
                .yearIndex(input.getYearIndex())
                .quantity(input.getQuantity());
        if (input.getObjectId() != null) {
            builder.specification(input.getObjectId().getDimensionId(), input.getObjectId().getObjectNumber());
        }
        return builder.apply();
    }

    private PeriodicalBudget createPeriodicalBudget(PeriodicalBudgetDTO input) {
        return PeriodicalBudget.of(input.getYearIndex(), YearMonth.parse(input.getPeriod()), input.getAmount());
    }

    private Voucher createVoucher(VoucherDTO input) {
        Voucher.Builder builder = Voucher.builder()
                .date(LocalDate.parse(input.getDate()))
                .series(input.getSeries())
                .number(input.getNumber())
                .signature(input.getSignature())
                .registrationDate(Optional.ofNullable(input.getRegistrationDate()).map(LocalDate::parse).orElse(null))
                .text(input.getText());
        input.getTransactions().stream().map(this::createTransaction).forEach(builder::addTransaction);
        return builder.apply();
    }

    private Transaction createTransaction(TransactionDTO input) {
        Transaction.Builder builder = Transaction.builder()
                .accountNumber(input.getAccountNumber())
                .amount(input.getAmount())
                .quantity(input.getQuantity())
                .signature(input.getSignature())
                .text(input.getText())
                .date(Optional.ofNullable(input.getDate()).map(LocalDate::parse).orElse(null));
        input.getCostCenterIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.COST_CENTRE, num)).forEach(builder::addObjectId);
        input.getCostBearerIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.COST_BEARER, num)).forEach(builder::addObjectId);
        input.getProjectIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.PROJECT, num)).forEach(builder::addObjectId);
        return builder.apply();
    }

    private Boolean isEmpty(String string) {
        return string == null || string.isBlank();
    }

    private static String bytesToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }
}
