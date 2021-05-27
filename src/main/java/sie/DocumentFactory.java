package sie;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import sie.domain.*;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 *
 */
class DocumentFactory {

    static final Pattern OBJECT_ID_PATTERN = Pattern.compile("(\"?(\\d+)\"?\\s\"?([0-9a-zA-Z]+)\"?)+");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{8}");
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("[A-Z]{3}");
    private static final String REPLACE_STRING = "[\"\\{\\}]";
    private final String content;
    private final List<FinancialYear> years = new ArrayList<>();
    private final List<SieLog> logs = new ArrayList<>();
    private Document document;

    private DocumentFactory(String content) {
        this.content = content;
    }

    static DocumentFactory from(InputStream stream) {
        return from(SieReader.streamToString(stream));
    }

    static DocumentFactory from(String content) {
        DocumentFactory factory = new DocumentFactory(content);
        factory.parse();
        return factory;
    }

    public Document getDocument() {
        return document;
    }

    public List<SieLog> getLogs() {
        return logs;
    }

    public List<SieLog> getWarnings() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.WARNING)).collect(Collectors.toList());
    }

    public List<SieLog> getCriticalErrors() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.CRITICAL)).collect(Collectors.toList());
    }

    private void parse() {
        Document.Builder builder = Document.builder()
                .metaData(getMetaData())
                .accountingPlan(getAccountingPlan())
                .dimensions(getDimensions())
                .objects(getObjects())
                .vouchers(getVouchers());
        addChecksum(builder);
        document = builder.apply();
    }

    private void addChecksum(Document.Builder builder) {
        Document doc = builder.apply();
        builder.checksum(Checksum.calculate(doc));
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
            String yearString = getLineAsString(Entity.TAXATION_YEAR);
            String originalString = yearString;
            String message = "";
            if (!yearString.matches("\\d{4}")) {
                message = "Taxeringsår \"" + yearString + "\" ska bara innehålla årtal";
                yearString = yearString.replaceAll("\\D", "");
            }
            try {
                builder.taxationYear(Year.parse(yearString));
                if (!message.isEmpty()) {
                    addInfo(message, Entity.TAXATION_YEAR);
                }
            } catch (DateTimeParseException ex) {
                addWarning("Taxeringsår tas bort då \"" + originalString + "\" inte motsvarar ett årtal", Entity.TAXATION_YEAR);
            }
        }
        if (hasLine(Entity.FINANCIAL_YEAR)) {
            builder.financialYears(getFinancialYears());
        }
        if (hasLine(Entity.PERIOD_RANGE)) {
            if (getType().equals(Document.Type.E1) || getType().equals(Document.Type.I4)) {
                addInfo("Filer av typen " + getType() + " får inte innehålla den här taggen", Entity.PERIOD_RANGE);
            } else {
                builder.periodRange(LocalDate.parse(getLineAsString(Entity.PERIOD_RANGE), Entity.DATE_FORMAT));
            }
        }
        if (hasLine(Entity.CURRENCY)) {
            String curr = getLineAsString(Entity.CURRENCY);
            if (!CURRENCY_PATTERN.matcher(curr).matches()) {
                addWarning("Valuta ska anges enligt ISO-4217. Felaktigt format: " + curr, Entity.CURRENCY);
            } else {
                builder.currency(getLineAsString(Entity.CURRENCY));
            }
        }
        return builder.apply();
    }

    private List<Voucher> getVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        if (hasLine(Entity.VOUCHER) && Integer.valueOf(getType().getNumber()) < 4) {
            addWarning("Filer av typen " + getType() + " får inte innehålla verifikationer", Entity.VOUCHER);
            return vouchers;
        }
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
            Optional<String> optVoucherNumber = Optional.ofNullable(parts.get(2) == null || parts.get(2).replaceAll(REPLACE_STRING, "").isEmpty()
                    ? null : parts.get(2).replaceAll(REPLACE_STRING, ""));
            if (getType().equals(Document.Type.I4) && optVoucherNumber.isPresent()) {
                addInfo("Filer av typen " + getType() + " bör inte innehålla verifikationsnummer", Entity.VOUCHER);
            } else {
                optVoucherNumber.map(Integer::valueOf).ifPresent(builder::number);
            }
            if (parts.size() > 3) {
                String dateString = parts.get(3).replaceAll(REPLACE_STRING, "");
                if (dateString.contains("-")) {
                    addInfo("Datum ska anges med åtta siffror - ååååmmdd utan bindestreck", Entity.VOUCHER);
                    dateString = dateString.replaceAll("-", "");
                }
                if (dateString.length() == 6) {
                    addInfo("Datum ska anges med åtta siffror - ååååmmdd - inte sex: \"" + dateString + "\"", Entity.VOUCHER);
                    dateString = "20" + dateString;
                }
                if (dateString.isEmpty()) {
                    SieException sieException = new SieException("Verifikationsdatum är tomt", Entity.VOUCHER);
                    addCritical(sieException);
                    throw sieException;
                }
                try {
                    builder.date(LocalDate.parse(dateString, Entity.DATE_FORMAT));
                } catch (DateTimeParseException e) {
                    SieException sieException = new SieException("Kan inte läsa verifikationsdatum: \"" + dateString + "\"", e, Entity.VOUCHER);
                    addCritical(sieException);
                    throw sieException;
                }
            } else {
                SieException sieException = new SieException("Verifikationsdatum saknas", Entity.VOUCHER);
                addCritical(sieException);
                throw sieException;
            }
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
            if (parts.size() < 2) {
                SieException ex = new SieException("Kontonummer och belopp saknas", Entity.TRANSACTION);
                addCritical(ex);
                throw ex;
            }
            Transaction.Builder tb = Transaction.builder();
            String accountNumber = parts.get(1).replaceAll(REPLACE_STRING, "");
            if (accountNumber.isEmpty()) {
                SieException ex = new SieException("Kontonummer saknas", Entity.TRANSACTION);
                addCritical(ex);
                throw ex;
            }
            tb.accountNumber(parts.get(1).replaceAll(REPLACE_STRING, ""));
            Matcher matcher = OBJECT_ID_PATTERN.matcher(parts.get(2));
            while (matcher.find()) {
                tb.addObjectId(Account.ObjectId.of(Integer.valueOf(matcher.group(2)), matcher.group(3)));
            }
            if (parts.size() < 4) {
                SieException ex = new SieException("Belopp saknas", Entity.TRANSACTION);
                addCritical(ex);
                throw ex;
            }
            String amount = parts.get(3);
            if (amount.contains(",")) {
                addInfo("Decimaltal måste anges med punkt.", Entity.TRANSACTION);
                amount = amount.replaceAll(",", ".");
            }
            try {
                tb.amount(new BigDecimal(amount));
            } catch (NumberFormatException e) {
                SieException ex = new SieException("Strängen \"" + amount + "\" för balans, konto " + accountNumber + ", kan inte hanteras som belopp", Entity.TRANSACTION);
                addCritical(ex);
                throw ex;
            }
            if (parts.size() > 4) {
                Optional.ofNullable(parts.get(4) == null || parts.get(4).replaceAll(REPLACE_STRING, "").isEmpty() ? null : parts.get(4).replaceAll(REPLACE_STRING, ""))
                        .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(tb::date);
            }
            if (parts.size() > 5) {
                Optional.ofNullable(parts.get(5) == null || handleQuotes(parts.get(5)).isEmpty() ? null : handleQuotes(parts.get(5)))
                        .ifPresent(tb::text);
            }
            if (parts.size() > 6) {
                String quantity = parts.get(6);
                Optional.ofNullable(quantity == null || quantity.replaceAll(REPLACE_STRING, "").isEmpty() ? null : quantity.replaceAll(REPLACE_STRING, ""))
                        .map(part -> {
                            if (part.contains(",")) {
                                addInfo("Decimaltal måste anges med punkt.", Entity.TRANSACTION);
                                part = part.replaceAll(",", ".");
                            }
                            return part;
                        })
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
                    String number = accountParts.get(1).replaceAll(REPLACE_STRING, "");
                    if (number == null || number.trim().isEmpty()) {
                        SieException ex = new SieException("Kontonummer får inte vara null eller tom sträng", Entity.ACCOUNT);
                        addCritical(ex);
                        throw ex;
                    }
                    if (!number.matches("\\d{4,}")) {
                        if (number.length() <= 3) {
                            addWarning(AccountingPlan.class, "Kontonummer ska innehålla minst fyra siffror: " + number, Entity.ACCOUNT);
                        } else {
                            addWarning(AccountingPlan.class, "Kontot har inte ett numeriskt värde: " + number, Entity.ACCOUNT);
                        }
                    }
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
        } else {
            addInfo("Kontoplanstyp saknas");
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
                    String amountString = l.get(5).replaceAll(REPLACE_STRING, "");
                    if (amountString.contains(",")) {
                        addInfo("Decimaltal måste anges med punkt.", Entity.PERIODICAL_BALANCE);
                        amountString = amountString.replaceAll(",", ".");
                    }
                    PeriodicalBalance.Builder pbBuilder = PeriodicalBalance.builder()
                            .yearIndex(yearIndex)
                            .period(period);
                    try {
                        BigDecimal amount = new BigDecimal(amountString);
                        pbBuilder.amount(amount);
                    } catch (NumberFormatException e) {
                        SieException ex = new SieException("Strängen \"" + amountString + "\" för periodbalans, konto " + number + ", kan inte hanteras som belopp", e, Entity.PERIODICAL_BALANCE);
                        addCritical(ex);
                        throw ex;
                    }
                    Matcher matcher = OBJECT_ID_PATTERN.matcher(l.get(4));
                    while (matcher.find()) {
                        pbBuilder.specification(Integer.valueOf(matcher.group(2)), matcher.group(3));
                    }
                    if (l.size() > 6) {
                        String quantity = l.get(6).replaceAll(REPLACE_STRING, "");
                        if (quantity.contains(",")) {
                            addInfo("Decimaltal måste anges med punkt.", Entity.PERIODICAL_BALANCE);
                            quantity = quantity.replaceAll(",", ".");
                        }
                        try {
                            pbBuilder.quantity(Double.valueOf(quantity));
                        } catch (NumberFormatException e) {
                            SieException ex = new SieException("Strängen \"" + quantity + "\" för kvantitet, konto " + number + ", kan inte hanteras som kvantitet", e, Entity.PERIODICAL_BALANCE);
                            addCritical(ex);
                            throw ex;
                        }
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
                    String amountString = l.get(l.size() - 1).replaceAll(REPLACE_STRING, "");
                    if (amountString.contains(",")) {
                        addInfo("Decimaltal måste anges med punkt.", Entity.PERIODICAL_BUDGET);
                        amountString = amountString.replaceAll(",", ".");
                    }
                    try {
                        BigDecimal amount = new BigDecimal(amountString);
                        Integer index = findFinancialYearByPeriod(period).map(FinancialYear::getIndex).orElse(Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
                        PeriodicalBudget budget = PeriodicalBudget.of(index, period, amount);
                        accountBuilder.addPeriodicalBudget(budget);
                    } catch (NumberFormatException e) {
                        SieException ex = new SieException("Strängen \"" + amountString + "\" för periodbudget, konto " + number + ", kan inte hanteras som belopp", e, Entity.PERIODICAL_BUDGET);
                        addCritical(ex);
                        throw ex;
                    }
                    break;
            }
        });
    }

    private void handleAccountObjectBalance(String number, Account.Builder accountBuilder) {
        getLineParts(number, 2, Entity.OBJECT_OPENING_BALANCE, Entity.OBJECT_CLOSING_BALANCE).stream().forEach(l -> {
            ObjectBalance.Builder obBuilder = ObjectBalance.builder()
                    .yearIndex(Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
            Matcher matcher = OBJECT_ID_PATTERN.matcher(l.get(3));
            if (matcher.find()) {
                String amountString = l.get(4).replaceAll(REPLACE_STRING, "");
                if (amountString.contains(",")) {
                    addInfo("Decimaltal måste anges med punkt.", Entity.PERIODICAL_BALANCE);
                    amountString = amountString.replaceAll(",", ".");
                }
                try {
                    BigDecimal amount = new BigDecimal(amountString);
                    obBuilder.amount(amount);
                } catch (NumberFormatException e) {
                    SieException ex = new SieException("Strängen \"" + amountString + "\" för objektbalans, konto " + number + ", kan inte hanteras som belopp", e, Entity.PERIODICAL_BALANCE);
                    addCritical(ex);
                    throw ex;
                }
                obBuilder.objectId(Integer.valueOf(matcher.group(2)), matcher.group(3));
            }
            if (l.size() > 5) {
                String quantity = l.get(5).replaceAll(REPLACE_STRING, "");
                if (quantity.contains(",")) {
                    addInfo("Decimaltal måste anges med punkt.", Entity.PERIODICAL_BALANCE);
                    quantity = quantity.replaceAll(",", ".");
                }
                try {
                    obBuilder.quantity(Double.valueOf(quantity));
                } catch (NumberFormatException e) {
                    SieException ex = new SieException("Strängen \"" + quantity + "\" för kvantitet, konto " + number + ", kan inte hanteras som kvantitet", e, Entity.PERIODICAL_BALANCE);
                    addCritical(ex);
                    throw ex;
                }
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
            String tag = l.get(0).trim();
            String amountString = l.get(3).replaceAll(REPLACE_STRING, "");
            if (amountString.contains(",")) {
                addInfo("Decimaltal måste anges med punkt.", tag);
                amountString = amountString.replaceAll(",", ".");
            }
            try {
                BigDecimal amount = new BigDecimal(amountString);
                Balance balance = Balance.of(amount, Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
                switch (tag.replaceAll("#", "")) {
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
            } catch (NumberFormatException ex) {
                SieException sieException = new SieException("Strängen \"" + amountString + "\" för balans, konto " + number + ", kan inte hanteras som belopp", ex, tag);
                addCritical(sieException);
                throw sieException;
            }
        });
    }

    private List<AccountingDimension> getDimensions() {
        List<AccountingDimension> dimList = getLinesParts(Entity.DIMENSION).stream().map(line -> {
            if (line.size() > 3) {
                return AccountingDimension.of(Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                        line.get(2).replaceAll(REPLACE_STRING, ""),
                        Integer.valueOf(line.get(3).replaceAll(REPLACE_STRING, "")));
            }
            return AccountingDimension.of(Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                    line.get(2).replaceAll(REPLACE_STRING, ""));
        }).collect(Collectors.toList());
        if (!dimList.isEmpty() && getType().equals(Document.Type.E1) || getType().equals(Document.Type.E2)) {
            addWarning("Filer av typen " + getType() + " får inte innehålla taggen", Entity.DIMENSION);
            return Collections.emptyList();
        }
        return dimList;
    }

    private List<AccountingObject> getObjects() {
        List<AccountingObject> objList = getLinesParts(Entity.OBJECT).stream().map(line -> {
            return AccountingObject.of(Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                    line.get(2).replaceAll(REPLACE_STRING, ""),
                    handleQuotes(line.get(3)));
        }).collect(Collectors.toList());
        if (!objList.isEmpty() && getType().equals(Document.Type.E1) || getType().equals(Document.Type.E2)) {
            addWarning("Filer av typen " + getType() + " får inte innehålla taggen", Entity.OBJECT);
            return Collections.emptyList();
        }
        return objList;
    }

    private Program getProgram() {
        if (!hasLine(Entity.PROGRAM)) {
            addWarning("Programinformation saknas", Entity.PROGRAM);
        }
        List<String> lineParts = getLineParts(Entity.PROGRAM);
        if (lineParts.size() < 2) {
            addWarning("Programnamn saknas", Entity.PROGRAM);
        }
        Boolean hasVersion = lineParts.size() > 2 && lineParts.get(2) != null && !handleQuotes(lineParts.get(2)).isEmpty();
        String version = Optional.ofNullable(hasVersion ? handleQuotes(lineParts.get(2)) : null).orElse(null);
        if (version == null) {
            addWarning("Programversion saknas", Entity.PROGRAM);
        }
        try {
            return Program.of(lineParts.get(1).replaceAll(REPLACE_STRING, ""), version);
        } catch (NullPointerException ex) {
            addWarning("Kunde inte skapa programinformation från raden", Entity.PROGRAM);
            return null;
        }
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
            if (getType().equals(Document.Type.I4)) {
                addInfo("Filer av typen " + getType() + " får inte innehålla den här taggen", Entity.COMPANY_SNI_CODE);
            } else {
                builder.sniCode(getLineAsString(Entity.COMPANY_SNI_CODE));
            }
        }
        if (hasLine(Entity.COMPANY_TYPE)) {
            builder.type(Company.Type.from(getLineParts(Entity.COMPANY_TYPE).get(1).replaceAll(REPLACE_STRING, "")));
        }
        if (hasLine(Entity.CORPORATE_ID)) {
            List<String> lineParts = getLineParts(Entity.CORPORATE_ID);
            getCorporateID(lineParts.get(1).replaceAll(REPLACE_STRING, "")).ifPresent(builder::corporateID);
            if (lineParts.size() > 2 && lineParts.get(2).replaceAll(REPLACE_STRING, "").trim().matches("\\d+")) {
                builder.aquisitionNumber(Integer.valueOf(lineParts.get(2).replaceAll(REPLACE_STRING, "")));
            }
        }
        getAddress().ifPresent(builder::address);
        return builder.apply();
    }

    private Optional<String> getCorporateID(String corporateId) {
        if (corporateId == null || corporateId.trim().isEmpty()) {
            return Optional.empty();
        }
        if (corporateId.matches("\\d{8}-\\d{4}")) {
            corporateId = corporateId.substring(2);
            addInfo("Organisationsnummer ska vara av formatet nnnnnn-nnnn", Entity.CORPORATE_ID);
        }
        if (corporateId.matches("\\d{6}-\\d{4}")) {
            return Optional.of(corporateId);
        }
        if (corporateId.matches("\\d*")) {
            if (corporateId.length() > 10) {
                corporateId = corporateId.substring(corporateId.length() - 10);
            }
        }
        Optional<String> result = Optional.of(corporateId).filter(cid -> cid.matches("\\d{10}")).map(cid -> cid.substring(0, 6) + "-" + cid.substring(6));
        if (result.isPresent()) {
            addInfo("Organisationsnummer ska vara av formatet nnnnnn-nnnn", Entity.CORPORATE_ID);
        } else {
            addWarning("Organisationsnummer är inte av formatet nnnnnn-nnnn: " + corporateId, Entity.CORPORATE_ID);
        }
        return result;
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
        IntStream.range(0, years.size() - 1).forEach(i -> {
            LocalDate start = years.get(i).getStartDate();
            LocalDate end = years.get(i + 1).getEndDate();
            if (!start.equals(end.plusDays(1))) {
                SieException ex = new SieException("Slutdatum för år " + years.get(i + 1).getIndex() + " är inte direkt före nästa års startdatum", Entity.FINANCIAL_YEAR);
                addCritical(ex);
                throw ex;
            }
        });
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
        if (!hasLine(Entity.TYPE)) {
            return Document.Type.DEFAULT;
        }
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
        return Document.Type.DEFAULT;
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

    private void addCritical(SieException sieException) {
        logs.add(SieLog.of(getClass(), sieException));
    }

    private void addWarning(String message, String tag) {
        addWarning(Document.class, message, tag);
    }

    private void addWarning(Class origin, String message, String tag) {
        if (!tag.startsWith("#")) {
            tag = "#" + tag;
        }
        logs.add(SieLog.warning(origin, message, tag));
    }

    private void addInfo(String message) {
        addInfo(Document.class, message, null);
    }

    private void addInfo(String message, String tag) {
        addInfo(Document.class, message, tag);
    }

    private void addInfo(Class origin, String message, String tag) {
        if (tag != null && !tag.startsWith("#")) {
            tag = "#" + tag;
        }
        logs.add(SieLog.info(origin, message, tag));
    }
}
