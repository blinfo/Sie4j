package sie;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import sie.domain.*;
import sie.exception.*;
import sie.log.SieLog;

/**
 *
 * @author Håkan Lidén
 *
 */
class DocumentFactory {

    static final Pattern OBJECT_ID_PATTERN = Pattern.compile("(\"?(\\d+)\"?\\s\"?([0-9a-zA-Z]+)\"?)+");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{8}");
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("[A-Z]{3}");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("\\d+");
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\d{4}");
    private static final Pattern VOUCHER_LINE_MISSING_SERIES_PATTERN = Pattern.compile("#VER\\s{2}\\d+ 20\\d{6} .*");
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");
    private static final String REPLACE_STRING = "[\"\\{\\}]";
    private final String content;
    private final List<FinancialYear> years = new ArrayList<>();
    private final List<SieLog> logs = new ArrayList<>();
    private Document document;
    private boolean conversion = true;

    private DocumentFactory(String content) {
        this.content = content.replaceAll("\r", "");
    }

    static DocumentFactory from(String content) {
        DocumentFactory factory = new DocumentFactory(content);
        factory.parse();
        return factory;
    }

    static DocumentFactory from(byte[] source) {
        return from(SieReader.byteArrayToString(source));
    }

    static DocumentFactory validation(String content) {
        DocumentFactory factory = new DocumentFactory(content);
        factory.setValidation();
        factory.parse();
        return factory;
    }

    private void setValidation() {
        this.conversion = false;
    }

    private boolean isConversion() {
        return conversion;
    }

    public Document getDocument() {
        if (isConversion() && !getCriticalErrors().isEmpty()) {
            throw new InvalidDocumentException(getCriticalErrors());
        }
        return document;
    }

    public List<SieLog> getLogs() {
        return logs.stream().sorted().toList();
    }

    public List<SieLog> getWarnings() {
        return logs.stream()
                .filter(log -> log.getLevel().equals(SieLog.Level.WARNING))
                .sorted()
                .toList();
    }

    public List<SieLog> getCriticalErrors() {
        return logs.stream()
                .filter(log -> log.getLevel().equals(SieLog.Level.CRITICAL))
                .sorted()
                .toList();
    }

    private void parse() {
        Document.Builder builder = Document.builder()
                .metaData(getMetaData())
                .accountingPlan(getAccountingPlan())
                .dimensions(getDimensions())
                .objects(getObjects())
                .vouchers(getVouchers());
        document = builder.apply();
        checkVoucherSeriesNumberLength();
    }

    private MetaData getMetaData() {
        MetaData.Builder builder = MetaData.builder();
        builder.program(getProgram());
        getGenerated().ifPresent(builder::generated);
        builder.read(isRead());
        builder.sieType(getType());
        getComments().ifPresent(builder::comments);
        builder.company(getCompany());
        if (hasLine(Entity.TAXATION_YEAR)) {
            String yearString = getLineAsString(Entity.TAXATION_YEAR);
            String originalString = yearString;
            String message = "";
            if (!YEAR_PATTERN.matcher(yearString).matches()) {
                message = "Taxeringsår '" + yearString + "' ska bara innehålla årtal";
                yearString = yearString.replaceAll("\\D", "");
            }
            try {
                builder.taxationYear(Year.parse(yearString));
                if (!message.isEmpty()) {
                    addInfo(message, Entity.TAXATION_YEAR, null);
                }
            } catch (DateTimeParseException ex) {
                addWarning("Taxeringsår tas bort då '" + originalString + "' inte motsvarar ett numeriskt årtal", Entity.TAXATION_YEAR, getLineFromTag(Entity.TAXATION_YEAR).orElse(null));
            }
        }
        if (hasLine(Entity.FINANCIAL_YEAR)) {
            builder.financialYears(getFinancialYears());
        }
        if (hasLine(Entity.PERIOD_RANGE)) {
            if (getType().equals(Document.Type.E1) || getType().equals(Document.Type.I4)) {
                addInfo("Filer av typen " + getType() + " får inte innehålla den här taggen", Entity.PERIOD_RANGE, getLineFromTag(Entity.PERIOD_RANGE).orElse(null));
            } else {
                builder.periodRange(LocalDate.parse(getLineAsString(Entity.PERIOD_RANGE), Entity.DATE_FORMAT));
            }
        }
        if (hasLine(Entity.CURRENCY)) {
            String curr = getLineAsString(Entity.CURRENCY);
            if (!CURRENCY_PATTERN.matcher(curr).matches()) {
                addWarning("Valuta ska anges enligt ISO-4217. Felaktigt format: " + curr, Entity.CURRENCY, getLineFromTag(Entity.CURRENCY).orElse(null));
            } else {
                builder.currency(getLineAsString(Entity.CURRENCY));
            }
        }
        return builder.apply();
    }

    private List<Voucher> getVouchers() {
        List<Voucher> vouchers = new ArrayList<>();
        if (hasLine(Entity.VOUCHER) && getType().getNumber() < 4) {
            addWarning("Filer av typen " + getType() + " får inte innehålla verifikationer", Entity.VOUCHER, null);
            return vouchers;
        }
        List<String> lines = Stream.of(content.split("\n"))
                .filter(this::isVoucherOrTransactionLine)
                .map(this::handleMissingVoucherNumberSeries)
                .toList();
        Voucher.Builder builder = null;
        if (lines.stream().collect(Collectors.joining()).contains("#" + Entity.TRANSACTION)) {
            for (String line : lines) {
                List<String> parts = StringUtil.getParts(line.trim());
                builder = handleVoucher(line, builder, vouchers, parts);
                handleTransaction(line, builder, parts);
            }
        }
        if (builder != null) {
            vouchers.add(builder.apply());
        }
        return vouchers;
    }

    private boolean isVoucherOrTransactionLine(String line) {
        return line.startsWith("#" + Entity.VOUCHER) || line.trim().startsWith("#" + Entity.TRANSACTION);
    }

    private String handleMissingVoucherNumberSeries(String line) {
        if (VOUCHER_LINE_MISSING_SERIES_PATTERN.matcher(line).matches()) {
            line = line.substring(0, 5) + "\"\"" + line.substring(5);
        }
        return line;
    }

    private Voucher.Builder handleVoucher(String line, Voucher.Builder builder, List<Voucher> vouchers, List<String> parts) {
        if (line.startsWith("#" + Entity.VOUCHER)) {
            if (builder != null) {
                vouchers.add(builder.apply());
            }
            builder = Voucher.builder();
            builder.line(line);
            int series = 1, voucherNum = 2, date = 3, comment = 4, registrationDate = 5, signature = 6;
            Optional.ofNullable(parts.get(series) == null || parts.get(series).isEmpty() ? null : parts.get(series).replaceAll(REPLACE_STRING, ""))
                    .ifPresent(builder::series);
            if (parts.size() > voucherNum + 1) {
                Optional<String> optVoucherNumber = Optional.ofNullable(parts.get(voucherNum) == null || parts.get(voucherNum).replaceAll(REPLACE_STRING, "").isEmpty()
                        ? null : parts.get(voucherNum).replaceAll(REPLACE_STRING, ""));
                if (getType().equals(Document.Type.I4) && optVoucherNumber.isPresent()) {
                    addInfo(Document.class, "Filer av typen " + getType() + " bör inte innehålla verifikationsnummer", Entity.VOUCHER, line);
                } else {
                    optVoucherNumber.map(Integer::valueOf).ifPresent(builder::number);
                }
            }
            if (parts.size() > date + 1) {
                String dateString = parts.get(date).replaceAll(REPLACE_STRING, "");
                if (dateString.contains("-")) {
                    addInfo("Datum ska anges med åtta siffror - ååååmmdd utan bindestreck", Entity.VOUCHER, line);
                    dateString = dateString.replaceAll("-", "");
                }
                if (dateString.length() == 6) {
                    addInfo("Datum ska anges med åtta siffror - ååååmmdd - inte sex: '" + dateString + "'", Entity.VOUCHER, line);
                    dateString = "20" + dateString;
                }
                if (dateString.isEmpty()) {
                    SieException ex = new MissingVoucherDateException();
                    addCritical(ex, line);
                }
                try {
                    builder.date(LocalDate.parse(dateString, Entity.DATE_FORMAT));
                } catch (DateTimeParseException e) {
                    SieException ex = new InvalidVoucherDateException(dateString, line, e);
                    addCritical(ex, line);
                }
            } else {
                SieException ex = new MissingVoucherDateException();
                addCritical(ex, line);
            }
            if (parts.size() > comment + 1) {
                Optional.ofNullable(parts.get(comment) == null || handleQuotes(parts.get(comment)).isEmpty() ? null : handleQuotes(parts.get(comment)))
                        .ifPresent(builder::text);
            }
            if (parts.size() > registrationDate + 1) {
                Optional.ofNullable(parts.get(registrationDate) == null || parts.get(registrationDate).isEmpty() || !DATE_PATTERN.matcher(parts.get(registrationDate)).matches() ? null : parts.get(registrationDate).replaceAll(REPLACE_STRING, ""))
                        .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(builder::registrationDate);
            }
            if (parts.size() > signature + 1) {
                Optional.ofNullable(parts.get(signature) == null || handleQuotes(parts.get(signature)).isEmpty() ? null : handleQuotes(parts.get(signature)))
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
                SieException ex = new MissingAccountNumberAndAmountException(Entity.TRANSACTION);
                addCritical(ex, line);
            }
            Transaction.Builder tb = Transaction.builder();
            String accountNumber = parts.get(1).replaceAll(REPLACE_STRING, "");
            if (accountNumber.isEmpty()) {
                SieException ex = new MissingAccountNumberException(Entity.TRANSACTION);
                addCritical(ex, line);
            }
            tb.accountNumber(parts.get(1).replaceAll(REPLACE_STRING, ""));
            if (parts.size() < 4) {
                SieException ex = new InvalidTransactionDataException(line);
                addCritical(ex, line);
            }
            Matcher matcher = OBJECT_ID_PATTERN.matcher(parts.get(2));
            while (matcher.find()) {
                tb.addObjectId(Account.ObjectId.of(Integer.valueOf(matcher.group(2)), matcher.group(3)));
            }
            if (parts.size() < 5) {
                SieException ex = new MissingAmountException(line);
                addCritical(ex, line);
            }
            String amount = parts.get(3);
            if (amount.contains(",")) {
                addInfo("Decimaltal måste anges med punkt", Entity.TRANSACTION, line);
                amount = amount.replaceAll(",", ".");
            }
            try {
                tb.amount(new BigDecimal(amount));
            } catch (NumberFormatException e) {
                SieException ex = new InvalidAmountException("Strängen '" + amount + "'för balans, konto " + accountNumber + ", kan inte hanteras som belopp", e, Entity.TRANSACTION);
                addCritical(ex, line);
            }
            if (parts.size() > 5) {
                Optional.ofNullable(parts.get(4) == null || parts.get(4).replaceAll(REPLACE_STRING, "").isEmpty() ? null : parts.get(4).replaceAll(REPLACE_STRING, ""))
                        .map(p -> LocalDate.parse(p, Entity.DATE_FORMAT)).ifPresent(tb::date);
            }
            if (parts.size() > 6) {
                Optional.ofNullable(parts.get(5) == null || handleQuotes(parts.get(5)).isEmpty() ? null : handleQuotes(parts.get(5)))
                        .ifPresent(tb::text);
            }
            if (parts.size() > 7) {
                String quantity = parts.get(6);
                Optional.ofNullable(quantity == null || quantity.replaceAll(REPLACE_STRING, "").isEmpty() ? null : quantity.replaceAll(REPLACE_STRING, ""))
                        .map(part -> {
                            if (part.contains(",")) {
                                addInfo("Decimaltal måste anges med punkt", Entity.TRANSACTION, line);
                                part = part.replaceAll(",", ".");
                            }
                            return part;
                        })
                        .map(Double::valueOf).ifPresent(tb::quantity);
            }
            if (parts.size() > 8) {
                Optional.ofNullable(parts.get(7) == null || handleQuotes(parts.get(7)).isEmpty() ? null : handleQuotes(parts.get(7)))
                        .ifPresent(tb::signature);
            }
            builder.addTransaction(tb.apply());
        }
    }

    private AccountingPlan getAccountingPlan() {
        List<Account> accounts = Stream.of(content.split("\n"))
                .parallel()
                .filter(line -> line.startsWith("#" + Entity.ACCOUNT))
                .map(line -> {
                    List<String> accountParts = StringUtil.getParts(line);
                    String number = accountParts.get(1).replaceAll(REPLACE_STRING, "");
                    if (number == null || number.isBlank()) {
                        SieException ex = new MissingAccountNumberException(Entity.ACCOUNT);
                        addCritical(ex, line);
                    } else if (!NUMERIC_PATTERN.matcher(number).matches()) {
                        SieException ex = new AccountNumberException("Kontot har inte ett numeriskt värde: " + number);
                        addCritical(ex, line);
                    } else if (!ACCOUNT_NUMBER_PATTERN.matcher(number).matches()) {
                        if (number.length() <= 3) {
                            addWarning(AccountingPlan.class, "Kontonummer ska innehålla minst fyra siffror: " + number, Entity.ACCOUNT, line);
                        } else if (number.length() > 4 && number.length() <= 6) {
                            addWarning(AccountingPlan.class, "Kontot har fler än fyra siffror: " + number, Entity.ACCOUNT, line);
                        } else if (number.length() > 6) {
                            SieException ex = new AccountNumberException("Kontot är längre än sex siffror: " + number + "\n " + line);
                            addCritical(ex, line);
                        }
                    }
                    try {
                        Account.Builder accountBuilder = Account.builder(number);
                        Optional.ofNullable(accountParts.size() > 2 ? accountParts.get(2) : null)
                                .map(label -> label.replaceAll(REPLACE_STRING, "")).ifPresent(accountBuilder::label);
                        handleSruAccountTypeAndUnit(number, accountBuilder);
                        handleAccountBalanceAndResult(number, accountBuilder);
                        handleAccountObjectBalance(number, accountBuilder);
                        handleAccountPeriodicalBudget(number, accountBuilder);
                        handleAccountPeriodicalBalance(number, accountBuilder);
                        return accountBuilder.apply();
                    } catch (SieException ex) {
                        addCritical(ex, line);
                        return null;
                    }
                })
                .filter(a -> a != null)
                .collect(Collectors.toList()); // Mutable list
        accounts.addAll(findMissingAccountNumbers().stream().map(number -> {
            addInfo("Konto " + number + " saknas i kontolistan", Entity.ACCOUNT, null);
            Account.Builder accountBuilder = Account.builder(number).label("Saknas vid import");
            handleSruAccountTypeAndUnit(number, accountBuilder);
            handleAccountBalanceAndResult(number, accountBuilder);
            handleAccountObjectBalance(number, accountBuilder);
            handleAccountPeriodicalBudget(number, accountBuilder);
            handleAccountPeriodicalBalance(number, accountBuilder);
            return accountBuilder.apply();
        }).toList());
        if (accounts.isEmpty()) {
            return null;
        }
        AccountingPlan.Builder builder = AccountingPlan.builder();
        if (hasLine(Entity.ACCOUNTING_PLAN_TYPE)) {
            builder.type(getLineAsString(Entity.ACCOUNTING_PLAN_TYPE));
        } else {
            addInfo("Kontoplanstyp saknas");
        }
        builder.accounts(accounts);
        return builder.apply();
    }

    private void handleSruAccountTypeAndUnit(String number, Account.Builder accountBuilder) {
        getLineParts(number, 1, Entity.SRU, Entity.ACCOUNT_TYPE, Entity.UNIT).stream().forEach(l -> {
            String tag = l.get(0).replaceAll("#", "");
            if (l.size() < 4 || l.get(2).isBlank()) {
                String line = l.get(l.size() - 1);
                addWarning("Raden ska ha tre delar men tredje delen saknas: '" + line + "'", tag, line);
            }
            switch (tag) {
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
            YearMonth period = YearMonth.parse(l.get(2).replaceAll(REPLACE_STRING, ""), Entity.YEAR_MONTH_FORMAT);
            // Ensure the right year index is provided
            Integer yearIndex = findFinancialYearIndexByPeriod(period).orElse(Integer.valueOf(l.get(1)));
            String amountString = l.get(5).replaceAll(REPLACE_STRING, "");
            if (amountString.contains(",")) {
                addInfo("Decimaltal måste anges med punkt", Entity.PERIODICAL_BALANCE, l.get(l.size() - 1));
                amountString = amountString.replaceAll(",", ".");
            }
            PeriodicalBalance.Builder pbBuilder = PeriodicalBalance.builder()
                    .yearIndex(yearIndex)
                    .period(period);
            try {
                BigDecimal amount = new BigDecimal(amountString);
                pbBuilder.amount(amount);
            } catch (NumberFormatException e) {
                SieException ex = new InvalidAmountException("Strängen '" + amountString + "' för periodbalans, konto " + number + ", kan inte hanteras som belopp\n " + l.get(l.size() - 1), e, Entity.PERIODICAL_BALANCE);
                addCritical(ex, l.get(l.size() - 1));
            }
            Matcher matcher = OBJECT_ID_PATTERN.matcher(l.get(4));
            while (matcher.find()) {
                pbBuilder.objectId(Integer.valueOf(matcher.group(2)), matcher.group(3));
            }
            if (l.size() > 7) {
                String quantity = l.get(6).replaceAll(REPLACE_STRING, "");
                if (!quantity.isBlank()) {
                    if (quantity.contains(",")) {
                        addInfo("Decimaltal måste anges med punkt", Entity.PERIODICAL_BALANCE, l.stream().collect(Collectors.joining(" ")));
                        quantity = quantity.replaceAll(",", ".");
                    }
                    try {
                        pbBuilder.quantity(Double.valueOf(quantity));
                    } catch (NumberFormatException e) {
                        SieException ex = new InvalidQuantityException("Strängen '" + quantity + "' för kvantitet, konto " + number + ", kan inte hanteras som kvantitet\n " + l.get(l.size() - 1), e, Entity.PERIODICAL_BALANCE);
                        addCritical(ex, l.get(l.size() - 1));
                    }
                }
            }
            accountBuilder.addPeriodicalBalance(pbBuilder.apply());
        });
    }

    private void handleAccountPeriodicalBudget(String number, Account.Builder accountBuilder) {
        getLineParts(number, 3, Entity.PERIODICAL_BUDGET).stream().forEach(l -> {
            switch (l.get(0).replaceAll("#", "")) {
                case Entity.PERIODICAL_BUDGET:
                    YearMonth period = YearMonth.parse(l.get(2).replaceAll(REPLACE_STRING, ""), Entity.YEAR_MONTH_FORMAT);
                    String amountString = l.get(l.size() - 2).replaceAll(REPLACE_STRING, "");
                    if (amountString.contains(",")) {
                        addInfo("Decimaltal måste anges med punkt", Entity.PERIODICAL_BUDGET, l.get(l.size() - 1));
                        amountString = amountString.replaceAll(",", ".");
                    }
                    try {
                        BigDecimal amount = new BigDecimal(amountString);
                        Integer yearIndex = findFinancialYearIndexByPeriod(period).orElse(Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
                        PeriodicalBudget budget = PeriodicalBudget.of(l.get(l.size() - 2), yearIndex, period, amount);
                        accountBuilder.addPeriodicalBudget(budget);
                    } catch (NumberFormatException e) {
                        SieException ex = new InvalidAmountException("Strängen '" + amountString + "' för periodbudget, konto " + number + ", kan inte hanteras som belopp\n " + l.get(l.size() - 1), e, Entity.PERIODICAL_BUDGET);
                        addCritical(ex, l.get(l.size() - 1));
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
                    addInfo("Decimaltal måste anges med punkt", Entity.PERIODICAL_BALANCE, l.get(l.size() - 1));
                    amountString = amountString.replaceAll(",", ".");
                }
                try {
                    BigDecimal amount = new BigDecimal(amountString);
                    obBuilder.amount(amount);
                } catch (NumberFormatException e) {
                    SieException ex = new InvalidAmountException("Strängen '" + amountString + "' för objektbalans, konto " + number + ", kan inte hanteras som belopp\n " + l.get(l.size() - 1), e, Entity.PERIODICAL_BALANCE);
                    addCritical(ex, l.get(l.size() - 1));
                }
                obBuilder.objectId(Integer.valueOf(matcher.group(2)), matcher.group(3));
            }
            if (l.size() > 6) {
                String quantity = l.get(5).replaceAll(REPLACE_STRING, "");
                if (quantity.contains(",")) {
                    addInfo("Decimaltal måste anges med punkt", Entity.PERIODICAL_BALANCE, l.get(l.size() - 1));
                    quantity = quantity.replaceAll(",", ".");
                }
                try {
                    obBuilder.quantity(Double.valueOf(quantity));
                } catch (NumberFormatException e) {
                    SieException ex = new InvalidQuantityException("Strängen '" + quantity + "' för kvantitet, konto " + number + ", kan inte hanteras som kvantitet\n " + l.get(l.size() - 1), e, Entity.PERIODICAL_BALANCE);
                    addCritical(ex, l.get(l.size() - 1));
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
                addInfo("Decimaltal måste anges med punkt", tag, l.get(l.size() - 1));
                amountString = amountString.replaceAll(",", ".");
            }
            try {
                BigDecimal amount = new BigDecimal(amountString);
                Balance balance = Balance.of(l.get(l.size() - 1), amount, Integer.valueOf(l.get(1).replaceAll(REPLACE_STRING, "")));
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
            } catch (NumberFormatException e) {
                SieException ex = new InvalidAmountException("Strängen '" + amountString + "' för balans, konto " + number + ", kan inte hanteras som belopp\n " + l.get(l.size() - 1), e, tag);
                addCritical(ex, l.get(l.size() - 1));
            }
        });
    }

    private List<AccountingDimension> getDimensions() {
        List<AccountingDimension> dimList = getLinesParts(Entity.DIMENSION).stream().map(line -> {
            Integer parentId = line.size() > 4 ? Integer.valueOf(line.get(3).replaceAll(REPLACE_STRING, "")) : null;
            return AccountingDimension.of(line.get(line.size() - 1), Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                    line.get(2).replaceAll(REPLACE_STRING, ""),
                    parentId);
        }).toList();
        if (!dimList.isEmpty() && getType().equals(Document.Type.E1) || getType().equals(Document.Type.E2)) {
            addWarning("Filer av typen " + getType() + " får inte innehålla taggen " + Entity.DIMENSION, Entity.DIMENSION, null);
            return List.of();
        }
        return dimList;
    }

    private List<AccountingObject> getObjects() {
        List<AccountingObject> objList = getLinesParts(Entity.OBJECT).stream().map(line -> {
            return AccountingObject.of(line.get(line.size() - 1), Integer.valueOf(line.get(1).replaceAll(REPLACE_STRING, "")),
                    line.get(2).replaceAll(REPLACE_STRING, ""),
                    handleQuotes(line.get(3)));
        }).toList();
        if (!objList.isEmpty() && getType().equals(Document.Type.E1) || getType().equals(Document.Type.E2)) {
            addWarning("Filer av typen " + getType() + " får inte innehålla taggen " + Entity.OBJECT, Entity.OBJECT, null);
            return List.of();
        }
        return objList;
    }

    private Program getProgram() {
        if (!hasLine(Entity.PROGRAM)) {
            addWarning("Programinformation saknas", Entity.PROGRAM, null);
        }
        List<String> lineParts = getLineParts(Entity.PROGRAM);
        if (lineParts.size() < 3) {
            addWarning("Programnamn saknas", Entity.PROGRAM, getLineFromTag(Entity.PROGRAM).orElse(null));
        }
        Boolean hasVersion = lineParts.size() > 3 && lineParts.get(2) != null && !handleQuotes(lineParts.get(2)).isEmpty();
        String version = Optional.ofNullable(hasVersion ? handleQuotes(lineParts.get(2)) : null).orElse(null);
        if (version == null) {
            addInfo("Programversion saknas", Entity.PROGRAM, getLineFromTag(Entity.PROGRAM).orElse(null));
        }
        try {
            return Program.of(lineParts.get(lineParts.size() - 1), lineParts.get(1).replaceAll(REPLACE_STRING, ""), version);
        } catch (NullPointerException ex) {
            addWarning("Kunde inte skapa programinformation från raden", Entity.PROGRAM, getLineFromTag(Entity.PROGRAM).orElse(null));
            return null;
        }
    }

    private Optional<Generated> getGenerated() {
        List<String> lineParts = getLineParts(Entity.GENERATED);
        String sign = Optional.ofNullable(lineParts.size() > 2 ? lineParts.get(2) : null)
                .map(s -> handleQuotes(s).isEmpty() ? null : handleQuotes(s)).orElse(null);
        String dateString = Optional.ofNullable(lineParts.size() > 1 ? lineParts.get(1) : null)
                .map(s -> handleQuotes(s).isBlank() ? null : handleQuotes(s)).orElse(null);
        if (dateString == null) {
            return Optional.empty();
        }
        LocalDate date = LocalDate.parse(dateString, Entity.DATE_FORMAT);
        return Optional.of(Generated.of(date, sign));
    }

    private Company getCompany() {
        Company.Builder builder = Company.builder(getLineAsString(Entity.COMPANY_NAME));
        if (hasLine(Entity.COMPANY_ID)) {
            builder.id(getLineAsString(Entity.COMPANY_ID));
        }
        if (hasLine(Entity.COMPANY_SNI_CODE)) {
            if (getType().equals(Document.Type.I4)) {
                addInfo("Filer av typen " + getType() + " får inte innehålla den här taggen", Entity.COMPANY_SNI_CODE, getLineFromTag(Entity.COMPANY_SNI_CODE).orElse(null));
            } else {
                builder.sniCode(getLineAsString(Entity.COMPANY_SNI_CODE));
            }
        }
        if (hasLine(Entity.COMPANY_TYPE)) {
            builder.type(Company.Type.from(getLineParts(Entity.COMPANY_TYPE).get(1).replaceAll(REPLACE_STRING, "")));
        }
        if (hasLine(Entity.CORPORATE_ID)) {
            List<String> lineParts = getLineParts(Entity.CORPORATE_ID);
            getCorporateID(lineParts.get(1).replaceAll(REPLACE_STRING, "")).ifPresent(builder::corporateId);
            if (lineParts.size() > 2 && lineParts.get(2).replaceAll(REPLACE_STRING, "").trim().matches("\\d+")) {
                builder.aquisitionNumber(Integer.valueOf(lineParts.get(2).replaceAll(REPLACE_STRING, "")));
            }
        }
        getAddress().ifPresent(builder::address);
        return builder.apply();
    }

    private Optional<String> getCorporateID(String corporateId) {
        if (corporateId == null || corporateId.isBlank()) {
            return Optional.empty();
        }
        String originalCID = corporateId;
        if (corporateId.matches("\\d{8}-\\d{4}")) {
            addInfo("Organisationsnummer ska vara av formatet nnnnnn-nnnn. " + originalCID, Entity.CORPORATE_ID, getLineFromTag(Entity.CORPORATE_ID).orElse(null));
            corporateId = corporateId.substring(2);
        }
        if (corporateId.matches("\\d{6}-\\d{4}")) {
            return Optional.of(corporateId);
        }
        if (corporateId.matches("\\d*")) {
            if (corporateId.length() > 10) {
                corporateId = corporateId.substring(corporateId.length() - 10);
            } else if (corporateId.length() < 10) {
                addInfo("Organisationsnummer är ogiltigt. " + originalCID, Entity.CORPORATE_ID, getLineFromTag(Entity.CORPORATE_ID).orElse(null));
                return Optional.empty();
            }
        }
        if (corporateId.matches("\\d*-\\d*")) {
            addInfo("Organisationsnummer ska vara av formatet nnnnnn-nnnn. \n", Entity.CORPORATE_ID, getLineFromTag(Entity.CORPORATE_ID).orElse(null));
        }
        Optional<String> result = Optional.of(corporateId).filter(cid -> cid.matches("\\d{10}")).map(cid -> cid.substring(0, 6) + "-" + cid.substring(6));
        if (result.isPresent()) {
            addInfo("Organisationsnummer ska vara av formatet nnnnnn-nnnn. " + originalCID, Entity.CORPORATE_ID, getLineFromTag(Entity.CORPORATE_ID).orElse(null));
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
        getLineFromTag(Entity.ADDRESS).ifPresent(builder::line);
        if (parts.size() > 2) {
            builder.contact(handleQuotes(parts.get(1)));
        }
        if (parts.size() > 3) {
            builder.streetAddress(handleQuotes(parts.get(2)));
        }
        if (parts.size() > 4) {
            builder.postalAddress(handleQuotes(parts.get(3)));
        }
        if (parts.size() > 5) {
            builder.phone(handleQuotes(parts.get(4)));
        }
        return builder.apply();
    }

    private List<FinancialYear> getFinancialYears() {
        if (years.isEmpty()) {
            years.addAll(getLinesParts(Entity.FINANCIAL_YEAR).stream()
                    .map(this::createFinancialYear)
                    .filter(fy -> fy != null)
                    .toList());
        }
        if (isConversion()) {
            IntStream.range(0, years.size() - 1).forEach(i -> {
                LocalDate start = years.get(i).startDate();
                LocalDate end = years.get(i + 1).endDate();
                if (!start.equals(end.plusDays(1))) {
                    throw new NonConsecutiveFinancialYearsException(years.get(i + 1));
                }
            });
        }
        return years;
    }

    private Optional<Integer> findFinancialYearIndexByPeriod(YearMonth period) {
        LocalDate date = LocalDate.of(period.getYear(), period.getMonth(), 5);
        return getFinancialYears().stream().filter(fy -> {
            return fy.startDate().isBefore(date) && fy.endDate().isAfter(date);
        }).map(FinancialYear::index).findFirst();
    }

    private FinancialYear createFinancialYear(List<String> parts) {
        try {
            Integer index = Integer.valueOf(parts.get(1).replaceAll(REPLACE_STRING, ""));
            LocalDate start = LocalDate.parse(parts.get(2).replaceAll(REPLACE_STRING, ""), Entity.DATE_FORMAT);
            LocalDate end = LocalDate.parse(parts.get(3).replaceAll(REPLACE_STRING, ""), Entity.DATE_FORMAT);
            return FinancialYear.of(parts.get(parts.size() - 1), index, start, end);
        } catch (DateTimeParseException | NumberFormatException ex) {
            SieException sieException = new SieException("Ogiltigt räkenskapsår.", ex);
            addCritical(sieException, parts.stream().collect(Collectors.joining(" ")));
            throw sieException;
        }
    }

    private List<String> findMissingAccountNumbers() {
        List<String> existingAccounts = getLinesParts(Entity.ACCOUNT).stream().map(s -> s.get(1)).toList();
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
        return referredAccounts.stream().filter(s -> !existingAccounts.contains(s)).toList();
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
            List<String> lineParts = getLineParts(Entity.COMMENTS);
            return Optional.of(handleQuotes(lineParts.get(1)));
        }
        return Optional.empty();
    }

    private void checkVoucherSeriesNumberLength() {
        if (document.vouchers().stream()
                .filter(v -> v.optSeries().orElse("").length() > 1)
                .findAny()
                .isPresent()) {
            addInfo("Filen innehåller verifikationsserie vars nummer är längre än ett tecken");
        }
    }

    private List<List<String>> getLinesParts(String prefix) {
        return Stream.of(content.split("\n"))
                .filter(line -> !line.isEmpty())
                .filter(line -> line.startsWith("#" + prefix))
                .map(line -> StringUtil.getParts(line))
                .toList();
    }

    private List<String> getLineParts(String prefix) {
        String line = content.substring(content.indexOf("#" + prefix));
        return StringUtil.getParts(line.substring(0, line.indexOf("\n")));
    }

    private String getLineAsString(String prefix) {
        return handleQuotes(getLineParts(prefix).stream().filter(p -> ignorePrefix(p)).collect(Collectors.joining(" ")).trim());
    }

    private Optional<String> getLineFromTag(String prefix) {
        return Stream.of(content.split("\n"))
                .filter(line -> !line.isBlank())
                .filter(line -> line.startsWith("#" + prefix))
                .findFirst();
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
                .toList();
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

    private void addCritical(SieException sieException, String line) {
        if (isConversion()) {
            throw sieException;
        }
        logs.add(SieLog.of(getClass(), sieException, line));
    }

    private void addWarning(String message, String tag, String line) {
        addWarning(Document.class, message, tag, line);
    }

    private void addWarning(Class origin, String message, String tag, String line) {
        tag = handleLogTag(tag);
        SieLog warning = SieLog.warning(origin, message, tag, line);
        if (!logs.contains(warning)) {
            logs.add(warning);
        }
    }

    private void addInfo(String message) {
        addInfo(Document.class, message, null, null);
    }

    private void addInfo(String message, String tag, String line) {
        addInfo(Document.class, message, tag, line);
    }

    private void addInfo(Class origin, String message, String tag, String line) {
        tag = handleLogTag(tag);
        SieLog info = SieLog.info(origin, message, tag, line);
        if (!logs.contains(info)) {
            logs.add(info);
        }
    }

    private String handleLogTag(String tag) {
        if (tag != null && !tag.startsWith("#")) {
            tag = "#" + tag;
        }
        return tag;
    }
}
