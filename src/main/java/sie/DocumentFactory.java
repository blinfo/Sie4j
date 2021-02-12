package sie;

import sie.domain.Account;
import sie.domain.AccountingPlan;
import sie.domain.Address;
import sie.domain.Balance;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.Entity;
import sie.domain.FinancialYear;
import sie.domain.Generated;
import sie.domain.MetaData;
import sie.domain.PeriodicalBudget;
import sie.domain.Program;
import sie.domain.Transaction;
import sie.domain.Voucher;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Håkan Lidén
 *
 */
class DocumentFactory {

    private static final String REPLACE_STRING = "[\"\\{\\}]";
    private final String content;

    private DocumentFactory(String content) {
        this.content = content;
    }

    static Document parse(String content) {
        DocumentFactory factory = new DocumentFactory(content);
        Document.Builder builder = Document.builder();
        builder.metaData(factory.getMetaData());
        builder.vouchers(factory.getVouchers());
        builder.accountingPlan(factory.getAccountingPlan());
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
            builder.taxationYear(Year.parse(getLineAsString(Entity.TAXATION_YEAR).trim()));
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
            List<String> parts = getParts(line.trim());
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
                    Optional.ofNullable(parts.get(4) == null || parts.get(4).isEmpty() ? null : parts.get(4).replaceAll(REPLACE_STRING, ""))
                            .ifPresent(builder::text);
                }
                if (parts.size() > 5) {
                    Optional.ofNullable(parts.get(5) == null || parts.get(5).isEmpty() ? null : parts.get(5).replaceAll(REPLACE_STRING, ""))
                            .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(builder::registrationDate);
                }
                if (parts.size() > 6) {
                    Optional.ofNullable(parts.get(6) == null || parts.get(6).isEmpty() ? null : parts.get(6).replaceAll(REPLACE_STRING, ""))
                            .ifPresent(builder::signature);
                }
            }
            if (line.trim().startsWith("#" + Entity.TRANSACTION)) {
                if (builder == null) {
                    throw new SieException("No current voucher builder");
                }
                Transaction.Builder tb = Transaction.builder();
                tb.accountNumber(parts.get(1).replaceAll(REPLACE_STRING, ""));
                tb.amount(new BigDecimal(parts.get(3)));
                if (parts.size() > 4) {
                    Optional.ofNullable(parts.get(4) == null || parts.get(4).isEmpty() ? null : parts.get(4).replaceAll(REPLACE_STRING, ""))
                            .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(tb::date);
                }
                if (parts.size() > 5) {
                    Optional.ofNullable(parts.get(5) == null || parts.get(5).isEmpty() ? null : parts.get(5).replaceAll(REPLACE_STRING, ""))
                            .ifPresent(tb::text);
                }
                if (parts.size() > 6) {
                    Optional.ofNullable(parts.get(6) == null || parts.get(6).isEmpty() || parts.get(6).equals("\"\"") ? null : parts.get(6).replaceAll(REPLACE_STRING, ""))
                            .map(Double::valueOf).ifPresent(tb::quantity);
                }
                if (parts.size() > 7) {
                    Optional.ofNullable(parts.get(7) == null || parts.get(7).isEmpty() ? null : parts.get(7).replaceAll(REPLACE_STRING, ""))
                            .ifPresent(tb::signature);
                }
                builder.transaction(tb.apply());
            }
        }
        if (builder != null) {
            vouchers.add(builder.apply());
        }
        return vouchers;
    }

    private AccountingPlan getAccountingPlan() {
        List<Account> accounts = Stream.of(content.split("\n"))
                .filter(line -> line.startsWith("#" + Entity.ACCOUNT))
                .map(line -> {
                    List<String> accountParts = getParts(line);
                    Account.Builder accountBuilder = Account.builder();
                    accountBuilder.number(accountParts.get(1));
                    Optional.ofNullable(accountParts.get(2)).map(label -> label.replaceAll(REPLACE_STRING, "")).ifPresent(accountBuilder::label);
                    getLineParts(accountParts.get(1), 1, Entity.SRU, Entity.ACCOUNT_TYPE, Entity.UNIT).stream().forEach(l -> {
                        switch (l.get(0).replaceAll("#", "")) {
                            case Entity.SRU:
                                accountBuilder.sruCode(l.get(2).replaceAll(REPLACE_STRING, ""));
                                break;
                            case Entity.UNIT:
                                accountBuilder.unit(l.get(2).replaceAll(REPLACE_STRING, ""));
                                break;
                            case Entity.ACCOUNT_TYPE:
                                Account.Type.find(l.get(2)).ifPresent(accountBuilder::type);
                                break;
                        }
                    });
                    getLineParts(accountParts.get(1), 2, Entity.OPENING_BALANCE, Entity.CLOSING_BALANCE, Entity.RESULT).stream().forEach(l -> {
                        switch (l.get(0).replaceAll("#", "")) {
                            case Entity.OPENING_BALANCE:
                                Balance opening = Balance.of(new BigDecimal(l.get(3)), Integer.valueOf(l.get(1)));
                                accountBuilder.openingBalance(opening);
                                break;
                            case Entity.CLOSING_BALANCE:
                                Balance closing = Balance.of(new BigDecimal(l.get(3)), Integer.valueOf(l.get(1)));
                                accountBuilder.closingBalance(closing);
                                break;
                            case Entity.RESULT:
                                Balance result = Balance.of(new BigDecimal(l.get(3)), Integer.valueOf(l.get(1)));
                                accountBuilder.result(result);
                                break;
                        }
                    });
                    getLineParts(accountParts.get(1), 3, Entity.PERIODICAL_BUDGET).stream().forEach(l -> {
                        switch (l.get(0).replaceAll("#", "")) {
                            case Entity.PERIODICAL_BUDGET:
                                PeriodicalBudget budget = PeriodicalBudget.of(Integer.valueOf(l.get(1)),
                                        YearMonth.parse(l.get(2), Entity.YEAR_MONTH_FORMAT), new BigDecimal(l.get(l.size() - 1)));
                                accountBuilder.periodicalBudget(budget);
                                break;
                        }
                    });
                    return accountBuilder.apply();
                }).collect(Collectors.toList());
        if (accounts == null || accounts.isEmpty()) {
            return null;
        }
        AccountingPlan.Builder builder = AccountingPlan.builder();
        if (hasLine(Entity.ACCOUNTING_PLAN_TYPE)) {
            builder.type(getLineParts(Entity.ACCOUNTING_PLAN_TYPE).get(1).trim());
        }
        builder.accounts(accounts);
        return builder.apply();
    }

    private Program getProgram() {
        List<String> lineParts = getLineParts(Entity.PROGRAM);
        String version = Optional.ofNullable(lineParts.get(2)).map(s -> s.replaceAll(REPLACE_STRING, "")).orElse(null);
        return Program.of(lineParts.get(1).replaceAll(REPLACE_STRING, ""), version);
    }

    private Generated getGenerated() {
        List<String> lineParts = getLineParts(Entity.GENERATED);
        String sign = Optional.ofNullable(lineParts.get(2)).map(s -> s.replaceAll(REPLACE_STRING, "")).orElse(null);
        return Generated.of(LocalDate.parse(lineParts.get(1), Entity.DATE_FORMAT), sign);
    }

    private Company getCompany() {
        Company.Builder builder = Company.builder();
        builder.name(getLineAsString(Entity.COMPANY_NAME));
        if (hasLine(Entity.COMPANY_ID)) {
            builder.id(getLineAsString(Entity.COMPANY_ID));
        }
        if (hasLine(Entity.COMPANY_SNI_CODE)) {
            builder.sniCode(getLineAsString(Entity.COMPANY_SNI_CODE));
        }
        if (hasLine(Entity.COMPANY_TYPE)) {
            builder.type(Company.Type.from(getLineParts(Entity.COMPANY_TYPE).get(1)));
        }
        if (hasLine(Entity.CORPORATE_ID)) {
            builder.corporateID(getLineAsString(Entity.CORPORATE_ID));
        }
        getAddress().ifPresent(builder::address);
        return builder.apply();
    }

    private Optional<Address> getAddress() {
        if (!hasLine(Entity.ADDRESS)) {
            return Optional.empty();
        }
        Address.Builder builder = Address.builder();
        List<String> parts = getLineParts(Entity.ADDRESS);
        builder.contact(parts.get(1).replaceAll(REPLACE_STRING, ""));
        builder.streetAddress(parts.get(2).replaceAll(REPLACE_STRING, ""));
        builder.postalAddress(parts.get(3).replaceAll(REPLACE_STRING, ""));
        builder.phone(parts.get(4).replaceAll(REPLACE_STRING, ""));
        return Optional.of(builder.apply());
    }

    private List<FinancialYear> getFinancialYears() {
        return getLinesParts(Entity.FINANCIAL_YEAR).stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    private FinancialYear create(List<String> parts) {
        Integer index = Integer.valueOf(parts.get(1));
        LocalDate start = LocalDate.parse(parts.get(2), Entity.DATE_FORMAT);
        LocalDate end = LocalDate.parse(parts.get(3), Entity.DATE_FORMAT);
        return FinancialYear.of(index, start, end);
    }

    private Boolean isRead() {
        List<String> lineParts = getLineParts(Entity.READ);
        return lineParts.get(1).equals("1");
    }

    private Document.Type getType() {
        List<String> lineParts = getLineParts(Entity.TYPE);
        Integer value = Integer.valueOf(lineParts.get(1).trim());
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
            return Optional.of(getLineParts(Entity.COMMENTS).get(1));
        }
        return Optional.empty();
    }

    private List<List<String>> getLinesParts(String prefix) {
        return Stream.of(content.split("\n"))
                .filter(line -> line.startsWith("#" + prefix))
                .map(line -> getParts(line))
                .collect(Collectors.toList());
    }

    private List<String> getLineParts(String prefix) {
        String line = content.substring(content.indexOf("#" + prefix));
        return getParts(line.substring(0, line.indexOf("\n")));
    }

    private List<String> getParts(String line) {
        String[] chars = line.split("");
        boolean quote = false;
        boolean objArray = false;
        StringBuilder builder = new StringBuilder();
        for (String c : chars) {
            if (c.equals("\"")) {
                quote = !quote;
            }
            if (c.equals("{")) {
                objArray = true;
            }
            if (c.equals("}")) {
                objArray = false;
            }
            if (!quote && !objArray && (c.equals(" ") || c.equals("\t"))) {
                builder.append("\n");
            }
            builder.append(c);
        }
        return Stream.of(builder.toString().split("\n")).map(String::trim).collect(Collectors.toList());
    }

    private String getLineAsString(String prefix) {
        return getLineParts(prefix).stream().filter(p -> ignorePrefix(p)).collect(Collectors.joining(" ")).replaceAll(REPLACE_STRING, "");
    }

    private static boolean ignorePrefix(String p) {
        return !p.startsWith("#");
    }

    private boolean hasLine(String string) {
        return content.contains("#" + string);
    }

    private List<List<String>> getLineParts(String key, int keyIndex, String... prefixes) {
        return Stream.of(content.split("\n"))
                .filter(line -> {
                    return Stream.of(prefixes).filter(pre -> line.startsWith("#" + pre)).findAny().isPresent();
                })
                .map(this::getParts)
                .filter(line -> line.get(keyIndex).equals(key))
                .collect(Collectors.toList());
    }
}
