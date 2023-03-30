package sie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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
                .metaData(getMetaData(dto.metaData()))
                .dimensions(getDimensions(dto))
                .objects(getObjects(dto))
                .accountingPlan(getAccountingPlan(dto))
                .vouchers(getVouchers(dto)).apply();
    }

    private MetaData getMetaData(MetaDataDTO dto) {
        return MetaData.builder()
                .company(createCompany(dto.company()))
                .currency(dto.currency())
                .financialYears(dto.financialYears().stream().map(this::createFinancialYear).collect(Collectors.toList()))
                .generated(createGenerated(dto.generated()))
                .periodRange(Optional.ofNullable(dto.periodRange()).orElse(null))
                .program(createProgram(dto.program()))
                .read(dto.read())
                .sieType(Optional.ofNullable(dto.sieType() == null ? null : dto.sieType().type()).map(Document.Type::get).orElse(null))
                .taxationYear(Optional.ofNullable(dto.taxationYear()).orElse(null))
                .apply();
    }

    private List<AccountingDimension> getDimensions(DocumentDTO dto) {
        return dto.dimensions().stream()
                .map(this::createAccountingDimension)
                .toList();
    }

    private List<AccountingObject> getObjects(DocumentDTO dto) {
        return dto.objects().stream()
                .map(this::createAccountingObject)
                .toList();
    }

    private AccountingPlan getAccountingPlan(DocumentDTO dto) {
        if (dto.accountingPlan() == null) {
            return null;
        }
        return AccountingPlan.builder()
                .accounts(dto.accountingPlan().accounts().stream().map(this::createAccount).toList())
                .type(dto.accountingPlan().type())
                .apply();
    }

    private List<Voucher> getVouchers(DocumentDTO dto) {
        return dto.vouchers().stream()
                .map(this::createVoucher)
                .toList();
    }

    private FinancialYear createFinancialYear(FinancialYearDTO input) {
        return FinancialYear.of(input.index(), input.startDate(), input.endDate());
    }

    private Company createCompany(CompanyDTO company) {
        if (company == null || isEmpty(company.name()) && isEmpty(company.corporateId())) {
            return null;
        }
        return Company.builder(company.name())
                .corporateId(company.corporateId())
                .address(createAddress(company.address()))
                .aquisitionNumber(company.aquisitionNumber())
                .sniCode(company.sniCode())
                .id(company.id())
                .type(Optional.ofNullable(company.type()).map(t -> Company.Type.from(t.type())).orElse(null))
                .apply();
    }

    private Generated createGenerated(GeneratedDTO generated) {
        if (generated == null || generated.date() == null) {
            return null;
        }
        return Generated.of(generated.date(), generated.signature());
    }

    private Program createProgram(ProgramDTO program) {
        if (program == null || isEmpty(program.name())) {
            return Program.of("BL APP", "1.0");
        }
        return Program.of(program.name(), program.version());
    }

    private Address createAddress(AddressDTO address) {
        if (address == null) {
            return null;
        }
        Address.Builder builder = Address.builder();
        builder.line(address.line())
                .contact(address.contact())
                .streetAddress(address.streetAddress())
                .postalAddress(address.postalAddress())
                .phone(address.phone());
        if (!builder.isEmpty()) {
            return builder.apply();
        }
        return null;
    }

    private AccountingDimension createAccountingDimension(AccountingDimensionDTO input) {
        return AccountingDimension.of(input.id(), input.label(), input.parentId());
    }

    private AccountingObject createAccountingObject(AccountingObjectDTO input) {
        return AccountingObject.of(input.dimensionId(), input.number(), input.label());
    }

    private Account createAccount(AccountDTO input) {
        Account.Builder builder = Account.builder(input.number())
                .label(input.label())
                .unit(input.unit());
        input.sruCodes().forEach(builder::addSruCode);
        input.openingBalances().stream().map(this::createBalance).forEach(builder::addOpeningBalance);
        input.closingBalances().stream().map(this::createBalance).forEach(builder::addClosingBalance);
        input.results().stream().map(this::createBalance).forEach(builder::addResult);
        input.objectOpeningBalances().stream().map(this::createObjectBalance).forEach(builder::addObjectOpeningBalance);
        input.objectClosingBalances().stream().map(this::createObjectBalance).forEach(builder::addObjectClosingBalance);
        input.periodicalBalances().stream().map(this::createPeriodicalBalance).forEach(builder::addPeriodicalBalance);
        input.periodicalBudgets().stream().map(this::createPeriodicalBudget).forEach(builder::addPeriodicalBudget);
        return builder.apply();
    }

    private Balance createBalance(BalanceDTO input) {
        return Balance.of(input.amount(), input.yearIndex());
    }

    private ObjectBalance createObjectBalance(ObjectBalanceDTO input) {
        return ObjectBalance.builder()
                .amount(input.amount())
                .yearIndex(input.yearIndex())
                .objectId(input.objectId().dimensionId(), input.objectId().objectNumber())
                .quantity(input.quantity())
                .apply();
    }

    private PeriodicalBalance createPeriodicalBalance(PeriodicalBalanceDTO input) {
        PeriodicalBalance.Builder builder = PeriodicalBalance.builder()
                .amount(input.amount())
                .period(input.period())
                .yearIndex(input.yearIndex())
                .quantity(input.quantity());
        if (input.objectId() != null) {
            builder.objectId(input.objectId().dimensionId(), input.objectId().objectNumber());
        }
        return builder.apply();
    }

    private PeriodicalBudget createPeriodicalBudget(PeriodicalBudgetDTO input) {
        return PeriodicalBudget.of(input.yearIndex(), input.period(), input.amount());
    }

    private Voucher createVoucher(VoucherDTO input) {
        Voucher.Builder builder = Voucher.builder()
                .date(input.date())
                .series(input.series())
                .number(input.number())
                .signature(input.signature())
                .registrationDate(Optional.ofNullable(input.registrationDate()).orElse(null))
                .text(input.text());
        input.transactions().stream().map(this::createTransaction).forEach(builder::addTransaction);
        return builder.apply();
    }

    private Transaction createTransaction(TransactionDTO input) {
        Transaction.Builder builder = Transaction.builder()
                .accountNumber(input.accountNumber())
                .amount(input.amount())
                .quantity(input.quantity())
                .signature(input.signature())
                .text(input.text())
                .date(Optional.ofNullable(input.date()).orElse(null));
        input.costCenterIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.COST_CENTRE, num)).forEach(builder::addObjectId);
        input.costBearerIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.COST_BEARER, num)).forEach(builder::addObjectId);
        input.projectIds().stream().map(num -> Account.ObjectId.of(AccountingDimension.PROJECT, num)).forEach(builder::addObjectId);
        return builder.apply();
    }

    private Boolean isEmpty(String string) {
        return string == null || string.isBlank();
    }

    private static String bytesToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }
}
