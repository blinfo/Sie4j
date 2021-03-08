package sie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sie.domain.Account;
import sie.domain.AccountingDimension;
import sie.domain.AccountingObject;
import sie.domain.AccountingPlan;
import sie.domain.Address;
import sie.domain.Balance;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.Entity;
import sie.domain.FinancialYear;
import sie.domain.Generated;
import sie.domain.MetaData;
import sie.domain.ObjectBalance;
import sie.domain.PeriodicalBalance;
import sie.domain.PeriodicalBudget;
import sie.domain.Program;
import sie.domain.Transaction;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 *
 */
class DocumentFactory {

    static final Pattern OBJECT_ID_PATTERN = Pattern.compile("(\"?(\\d+)\"?\\s\"?([0-9a-zA-Z]+)\"?)+");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{8}");
    private static final String REPLACE_STRING = "[\"\\{\\}]";
    private final String content;
    private final List<FinancialYear> years = new ArrayList<>();

    DocumentFactory(String content) {
        this.content = content;
    }

    static Document parse(String content) {
        DocumentFactory factory = new DocumentFactory(content);
        Document.Builder builder = Document.builder()
                .metaData(factory.getMetaData())
                .accountingPlan(factory.getAccountingPlan())
                .dimensions(factory.getDimensions())
                .objects(factory.getObjects())
                .vouchers(factory.getVouchers());
        Document doc = builder.apply();
        builder.checksum(Checksum.calculate(doc));
        return builder.apply();
    }

    private MetaData getMetaData() {
        MetaData.Builder builder = MetaData.builder();
        builder.program(getProgram());
        builder.generated(getGenerated());
        builder.read(isRead());
        builder.sieType(getType());
        getComments().ifPresent(builder::comments);
        builder.company(getCompany());
        if (hasLine(Entity.TAXATION_YEAR)) {
            builder.taxationYear(Year.parse(getLineAsString(Entity.TAXATION_YEAR)));
        }
        if (hasLine(Entity.FINANCIAL_YEAR)) {
            builder.financialYears(getFinancialYears());
        }
        if (hasLine(Entity.PERIOD_RANGE)) {
            builder.periodRange(LocalDate.parse(getLineAsString(Entity.PERIOD_RANGE), Entity.DATE_FORMAT));
        }
        if (hasLine(Entity.CURRENCY)) {
            builder.currency(getLineAsString(Entity.CURRENCY));
        }
        return builder.apply();
    }

    private List<Voucher> getVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        List<String> lines = Stream.of(content.split("\n"))
                .filter(line -> {
                    String l = line.trim();
                    return l.startsWith("#" + Entity.VOUCHER) || l.startsWith("#" + Entity.TRANSACTION);
                }).collect(Collectors.toList());
        Voucher.Builder builder = null;
        for (String line : lines) {
            List<String> parts = StringUtil.getParts(line.trim());
            builder = handleVoucher(line, builder, vouchers, parts);
            handleTransaction(line, builder, parts);
        }
        if (builder != null) {
            vouchers.add(builder.apply());
        }
        return vouchers;
    }

    private Voucher.Builder handleVoucher(String line, Voucher.Builder builder, List<Voucher> vouchers, List<String> parts) {
        if (line.startsWith("#" + Entity.VOUCHER)) {
            if (builder != null) {
                vouchers.add(builder.apply());
            }
            builder = Voucher.builder();
            Optional.ofNullable(parts.get(1) == null || parts.get(1).isEmpty() ? null : parts.get(1).replaceAll(REPLACE_STRING, ""))
                    .ifPresent(builder::series);
            Optional.ofNullable(parts.get(2) == null || parts.get(2).replaceAll(REPLACE_STRING, "").isEmpty()
                    ? null : parts.get(2).replaceAll(REPLACE_STRING, ""))
                    .map(Integer::valueOf).ifPresent(builder::number);
            builder.date(LocalDate.parse(parts.get(3).replaceAll(REPLACE_STRING, ""), Entity.DATE_FORMAT));
            if (parts.size() > 4) {
                Optional.ofNullable(parts.get(4) == null || handleQuotes(parts.get(4)).isEmpty() ? null : handleQuotes(parts.get(4)))
                        .ifPresent(builder::text);
            }
            if (parts.size() > 5) {
                Optional.ofNullable(parts.get(5) == null || parts.get(5).isEmpty() || !DATE_PATTERN.matcher(parts.get(5)).matches() ? null : parts.get(5).replaceAll(REPLACE_STRING, ""))
                        .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(builder::registrationDate);
            }
            if (parts.size() > 6) {
                Optional.ofNullable(parts.get(6) == null || handleQuotes(parts.get(6)).isEmpty() ? null : handleQuotes(parts.get(6)))
                        .ifPresent(builder::signature);
            }
        }
        return builder;
    }

    private void handleTransaction(String line, Voucher.Builder builder, List<String> parts) throws SieException, NumberFormatException {
        if (line.trim().startsWith("#" + Entity.TRANSACTION)) {
            if (builder == null) {
                throw new SieException("No current voucher builder");
            }
            Transaction.Builder tb = Transaction.builder();
            tb.accountNumber(parts.get(1).replaceAll(REPLACE_STRING, ""));
            Matcher matcher = OBJECT_ID_PATTERN.matcher(parts.get(2));
            while (matcher.find()) {
                tb.addObjectId(Account.ObjectId.of(Integer.valueOf(matcher.group(2)), matcher.group(3)));
            }
            tb.amount(new BigDecimal(parts.get(3)));
            if (parts.size() > 4) {
                Optional.ofNullable(parts.get(4) == null || parts.get(4).replaceAll(REPLACE_STRING, "").isEmpty() ? null : parts.get(4).replaceAll(REPLACE_STRING, ""))
                        .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(tb::date);
            }
            if (parts.size() > 5) {
                Optional.ofNullable(parts.get(5) == null || handleQuotes(parts.get(5)).isEmpty() ? null : handleQuotes(parts.get(5)))
                        .ifPresent(tb::text);
            }
            if (parts.size() > 6) {
                Optional.ofNullable(parts.get(6) == null || parts.get(6).replaceAll(REPLACE_STRING, "").isEmpty() ? null : parts.get(6).replaceAll(REPLACE_STRING, ""))
                        .map(part -> part.replaceAll(",", "."))
                        .map(Double::valueOf).ifPresent(tb::quantity);
            }
            if (parts.size() > 7) {
                Optional.ofNullable(parts.get(7) == null || handleQuotes(parts.get(7)).isEmpty() ? null : handleQuotes(parts.get(7)))
                        .ifPresent(tb::signature);
            }
            builder.addTransaction(tb.apply());
        }
    }

    private AccountingPlan getAccountingPlan() {
        List<Account> accounts = Stream.of(content.split("\n"))
                .filter(line -> line.startsWith("#" + Entity.ACCOUNT))
                .map(line -> {
                    List<String> accountParts = StringUtil.getParts(line);
                    String number = accountParts.get(1);
                    Account.Builder accountBuilder = Account.builder(number);
                    Optional.ofNullable(accountParts.size() > 2 ? accountParts.get(2) : null)
                            .map(label -> label.replaceAll(REPLACE_STRING, "")).ifPresent(accountBuilder::label);
                    handleSruAccountTypeAndUnit(number, accountBuilder);
                    handleAccountBalanceAndResult(number, accountBuilder);
                    handleAccountObjectBalance(number, accountBuilder);
                    handleAccountPeriodicalBudget(number, accountBuilder);
                    handleAccountPeriodicalBalance(number, accountBuilder);
                    return accountBuilder.apply();
                }).collect(Collectors.toList());
        accounts.addAll(findMissingAccountNumbers().stream().map(number -> {
            Account.Builder accountBuilder = Account.builder(number).label("Saknas vid import");
            handleSruAccountTypeAndUnit(number, accountBuilder);
            handleAccountBalanceAndResult(number, accountBuilder);
            handleAccountObjectBalance(number, accountBuilder);
            handleAccountPeriodicalBudget(number, accountBuilder);
            handleAccountPeriodicalBalance(number, accountBuilder);
            return accountBuilder.apply();
        }).collect(Collectors.toList()));
        if (accounts.isEmpty()) {
            return null;
        }
        AccountingPlan.Builder builder = AccountingPlan.builder();
        if (hasLine(Entity.ACCOUNTING_PLAN_TYPE)) {
            builder.type(getLineParts(Entity.ACCOUNTING_PLAN_TYPE).get(1).trim());
        }
        builder.accounts(accounts);
        return builder.apply();
    }

    private void handleSruAccountTypeAndUnit(String number, Account.Builder accountBuilder) {
        getLineParts(number, 1, Entity.SRU, Entity.ACCOUNT_TYPE, Entity.UNIT).stream().forEach(l -> {
            switch (l.get(0).replaceAll("#", "")) {
                case Entity.SRU:
                    accountBuilder.addSruCode(l.get(2).replaceAll(REPLACE_STRING, ""));
                    break;
                case Entity.UNIT:
                    accountBuilder.unit(l.get(2).replaceAll(REPLACE_STRING, ""));
                    break;
                case Entity.ACCOUNT_TYPE:
                    Account.Type.find(l.get(2).replaceAll(REPLACE_STRING, "")).ifPresent(accountBuilder::type);
                    break;
            }
        });
    }

    private void handleAccountPeriodicalBalance(String number, Account.Builder accountBuilder) {
        getLineParts(number, 3, Entity.PERIODICAL_BALANCE).stream().forEach(l -> {
            switch (l.get(0).replaceAll("#", "")) {
                case Entity.PERIODICAL_BALANCE:
                    YearMonth period = YearMonth.parse(l.get(2).replaceAll(REPLACE_STRING, ""), Entity.YEAR_MONTH_FORMAT);
                    // Ensure the right year index is provided
                    Integer yearIndex = findFinancialYearByPeriod(period).map(FinancialYear::getIndex).orElse(Integer.valueOf(l.get(1)));
                    PeriodicalBalance.Builder pbBuilder = PeriodicalBalance.builder()
                            .yearIndex(yearIndex)
                            .period(period)
                            .amount(new BigDecimal(l.get(5).replaceAll(REPLACE_STRING, "")));
                    Matcher matcher = OBJECT_ID_PATTERN.matcher(l.get(4));
                    while (matcher.find()) {
                        pbBuilder.specification(Integer.valueOf(matcher.group(2)), matcher.group(3));
                    }
                    if (l.size() > 6) {
                        pbBuilder.quantity(Double.valueOf(l.get(6).replaceAll(REPLACE_STRING, "")));
                    }
                    accountBuilder.addPeriodicalBalance(pbBuilder.apply());
                    break;

            }
        });
    }

    private void handleAccountPeriodicalBudget(String number, Account.Builder accountBuilder) {
        getLineParts(number, 3, Entity.PERIODICAL_BUDGET).stream().forEach(l -> {
            switch (l.get(0).replaceAll("#", "")) {
                case Entity.PERIODICAL_BUDGET:
                    YearMonth period = YearMonth.parse(l.get(2).replaceAll(REPLACE_STRING, ""), Entity.YEAR_MONTH_FORMAT);
                    BigDecimal amount = new BigDecimal(l.get(l.size() - 1).replaceAll(REPLACE_STRING, ""));
                    Integer index = findFinancialYearByPeriod(period).map(FinancialYear::getIndex).orElse(Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
                    PeriodicalBudget budget = PeriodicalBudget.of(index, period, amount);
                    accountBuilder.addPeriodicalBudget(budget);
                    break;
            }
        });
    }

    private void handleAccountObjectBalance(String number, Account.Builder accountBuilder) {
        getLineParts(number, 2, Entity.OBJECT_OPENING_BALANCE, Entity.OBJECT_CLOSING_BALANCE).stream().forEach(l -> {
            ObjectBalance.Builder obBuilder = ObjectBalance.builder()
                    .amount(new BigDecimal(l.get(4).replaceAll(REPLACE_STRING, "")))
                    .yearIndex(Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
            Matcher matcher = OBJECT_ID_PATTERN.matcher(l.get(3));
            if (matcher.find()) {
                obBuilder.objectId(Integer.valueOf(matcher.group(2)), matcher.group(3));
            }
            if (l.size() > 5) {
                obBuilder.quantity(Double.valueOf(l.get(5)));
            }
            switch (l.get(0).replaceAll("#", "")) {
                case Entity.OBJECT_OPENING_BALANCE:
                    accountBuilder.addObjectOpeningBalance(obBuilder.apply());
                    break;
                case Entity.OBJECT_CLOSING_BALANCE:
                    accountBuilder.addObjectOpeningBalance(obBuilder.apply());
                    break;
            }
        });
    }

    private void handleAccountBalanceAndResult(String number, Account.Builder accountBuilder) {
        getLineParts(number, 2, Entity.OPENING_BALANCE, Entity.CLOSING_BALANCE, Entity.RESULT).stream().forEach(l -> {
            Balance balance = Balance.of(new BigDecimal(l.get(3).replaceAll(REPLACE_STRING, "")), Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
            switch (l.get(0).replaceAll("#", "")) {
                case Entity.OPENING_BALANCE:
                    accountBuilder.addOpeningBalance(balance);
                    break;
                case Entity.CLOSING_BALANCE:
                    accountBuilder.addClosingBalance(balance);
                    break;
                case Entity.RESULT:
                    accountBuilder.addResult(balance);
                    break;
            }
        });
    }

    private List<AccountingDimension> getDimensions() {
        return getLinesParts(Entity.DIMENSION).stream().map(line -> {
            if (line.size() > 3) {
                return AccountingDimension.of(Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                        line.get(2).replaceAll(REPLACE_STRING, ""),
                        Integer.valueOf(line.get(3).replaceAll(REPLACE_STRING, "")));
            }
            return AccountingDimension.of(Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                    line.get(2).replaceAll(REPLACE_STRING, ""));
        }).collect(Collectors.toList());
    }

    private List<AccountingObject> getObjects() {
        return getLinesParts(Entity.OBJECT).stream().map(line -> {
            return AccountingObject.of(Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                    line.get(2).replaceAll(REPLACE_STRING, ""),
                    handleQuotes(line.get(3)));
        }).collect(Collectors.toList());
    }

    private Program getProgram() {
        List<String> lineParts = getLineParts(Entity.PROGRAM);
        String version = Optional.ofNullable(lineParts.get(2) == null || handleQuotes(lineParts.get(2)).isEmpty()
                ? null : handleQuotes(lineParts.get(2))).orElse(null);
        return Program.of(lineParts.get(1).replaceAll(REPLACE_STRING, ""), version);
    }

    private Generated getGenerated() {
        List<String> lineParts = getLineParts(Entity.GENERATED);
        String sign = Optional.ofNullable(lineParts.size() > 2 ? lineParts.get(2) : null)
                .map(s -> handleQuotes(s).isEmpty() ? null : handleQuotes(s)).orElse(null);
        return Generated.of(LocalDate.parse(lineParts.get(1).replaceAll(REPLACE_STRING, ""), Entity.DATE_FORMAT), sign);
    }

    private Company getCompany() {
        Company.Builder builder = Company.builder(getLineAsString(Entity.COMPANY_NAME));
        if (hasLine(Entity.COMPANY_ID)) {
            builder.id(getLineAsString(Entity.COMPANY_ID));
        }
        if (hasLine(Entity.COMPANY_SNI_CODE)) {
            builder.sniCode(getLineAsString(Entity.COMPANY_SNI_CODE));
        }
        if (hasLine(Entity.COMPANY_TYPE)) {
            builder.type(Company.Type.from(getLineParts(Entity.COMPANY_TYPE).get(1).replaceAll(REPLACE_STRING, "")));
        }
        if (hasLine(Entity.CORPORATE_ID)) {
            List<String> lineParts = getLineParts(Entity.CORPORATE_ID);
            builder.corporateID(lineParts.get(1).replaceAll(REPLACE_STRING, ""));
            if (lineParts.size() > 2 && lineParts.get(2).replaceAll(REPLACE_STRING, "").trim().matches("\\d+")) {
                builder.aquisitionNumber(Integer.valueOf(lineParts.get(2).replaceAll(REPLACE_STRING, "")));
            }
        }
        getAddress().ifPresent(builder::address);
        return builder.apply();
    }

    private Optional<Address> getAddress() {
        if (!hasLine(Entity.ADDRESS)) {
            return Optional.empty();
        }
        Address address = handleAddress(getLineParts(Entity.ADDRESS));
        return Optional.ofNullable(address.isEmpty() ? null : address);
    }

    private Address handleAddress(List<String> parts) {
        Address.Builder builder = Address.builder();
        if (parts.size() > 1) {
            builder.contact(handleQuotes(parts.get(1)));
        }
        if (parts.size() > 2) {
            builder.streetAddress(handleQuotes(parts.get(2)));
        }
        if (parts.size() > 3) {
            builder.postalAddress(handleQuotes(parts.get(3)));
        }
        if (parts.size() > 4) {
            builder.phone(handleQuotes(parts.get(4)));
        }
        return builder.apply();
    }

    private List<FinancialYear> getFinancialYears() {
        if (years.isEmpty()) {
            years.addAll(getLinesParts(Entity.FINANCIAL_YEAR).stream()
                    .map(this::createFinancialYear)
                    .collect(Collectors.toList()));
        }
        return years;
    }

    private Optional<FinancialYear> findFinancialYearByPeriod(YearMonth period) {
        LocalDate date = LocalDate.of(period.getYear(), period.getMonth(), 5);
        return getFinancialYears().stream().filter(fy -> {
            return fy.getStartDate().isBefore(date) && fy.getEndDate().isAfter(date);
        }).findFirst();
    }

    private FinancialYear createFinancialYear(List<String> parts) {
        Integer index = Integer.valueOf(parts.get(1).replaceAll(REPLACE_STRING, ""));
        LocalDate start = LocalDate.parse(parts.get(2).replaceAll(REPLACE_STRING, ""), Entity.DATE_FORMAT);
        LocalDate end = LocalDate.parse(parts.get(3).replaceAll(REPLACE_STRING, ""), Entity.DATE_FORMAT);
        return FinancialYear.of(index, start, end);
    }

    private List<String> findMissingAccountNumbers() {
        List<String> existingAccounts = getLinesParts(Entity.ACCOUNT).stream().map(s -> s.get(1)).collect(Collectors.toList());
        Set<String> referredAccounts = new HashSet<>();
        getLinesParts(Entity.ACCOUNT_TYPE).forEach(s -> referredAccounts.add(s.get(1).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.CLOSING_BALANCE).forEach(s -> referredAccounts.add(s.get(2).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.OBJECT_CLOSING_BALANCE).forEach(s -> referredAccounts.add(s.get(2).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.OBJECT_OPENING_BALANCE).forEach(s -> referredAccounts.add(s.get(2).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.OPENING_BALANCE).forEach(s -> referredAccounts.add(s.get(2).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.PERIODICAL_BALANCE).forEach(s -> referredAccounts.add(s.get(3).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.PERIODICAL_BUDGET).forEach(s -> referredAccounts.add(s.get(3).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.RESULT).forEach(s -> referredAccounts.add(s.get(2).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.SRU).forEach(s -> referredAccounts.add(s.get(1).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.TRANSACTION).forEach(s -> referredAccounts.add(s.get(1).replaceAll(REPLACE_STRING, "")));
        getLinesParts(Entity.UNIT).forEach(s -> referredAccounts.add(s.get(1).replaceAll(REPLACE_STRING, "")));
        return referredAccounts.stream().filter(s -> !existingAccounts.contains(s)).collect(Collectors.toList());
    }

    private Boolean isRead() {
        List<String> lineParts = getLineParts(Entity.READ);
        return lineParts.get(1).replaceAll(REPLACE_STRING, "").equals("1");
    }

    private Document.Type getType() {
        List<String> lineParts = getLineParts(Entity.TYPE);
        Integer value = Integer.valueOf(lineParts.get(1).replaceAll(REPLACE_STRING, "").trim());
        if (value < 4) {
            return Document.Type.valueOf("E" + value);
        }
        if (value == 4) {
            return content.contains("#" + Entity.OPENING_BALANCE)
                    || content.contains("#" + Entity.CLOSING_BALANCE)
                    || content.contains("#" + Entity.RESULT)
                    ? Document.Type.E4
                    : Document.Type.I4;
        }
        return Document.Type.E1;
    }

    private Optional<String> getComments() {
        if (hasLine(Entity.COMMENTS)) {
            return Optional.of(handleQuotes(getLineParts(Entity.COMMENTS).get(1)));
        }
        return Optional.empty();
    }

    private List<List<String>> getLinesParts(String prefix) {
        return Stream.of(content.split("\n"))
                .filter(line -> !line.isEmpty())
                .filter(line -> line.startsWith("#" + prefix))
                .map(line -> StringUtil.getParts(line))
                .collect(Collectors.toList());
    }

    private List<String> getLineParts(String prefix) {
        String line = content.substring(content.indexOf("#" + prefix));
        return StringUtil.getParts(line.substring(0, line.indexOf("\n")));
    }

    private String getLineAsString(String prefix) {
        return handleQuotes(getLineParts(prefix).stream().filter(p -> ignorePrefix(p)).collect(Collectors.joining(" ")).trim());
    }

    private static boolean ignorePrefix(String p) {
        return !p.startsWith("#");
    }

    private boolean hasLine(String string) {
        return content.contains("#" + string);
    }

    private List<List<String>> getLineParts(String key, int keyIndex, String... prefixes) {
        return Stream.of(content.split("\n"))
                .filter(line -> !line.isEmpty())
                .filter(line -> {
                    return Stream.of(prefixes).filter(pre -> line.startsWith("#" + pre)).findAny().isPresent();
                })
                .map(StringUtil::getParts)
                .filter(line -> line.get(keyIndex).equals(key))
                .collect(Collectors.toList());
    }

    private String handleQuotes(String input) {
        if (input == null) {
            return "";
        }
        String result = input;
        if (input.startsWith("\"")) {
            result = result.substring(1);
        }
        if (input.endsWith("\"") && !input.endsWith("\\\"")) {
            result = result.substring(0, result.length() - 1);
        }
        return result.replaceAll("\\\\\"", "\"").replaceAll("[{}]", "");
    }
}
