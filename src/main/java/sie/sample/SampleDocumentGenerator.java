package sie.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import sie.Sie4j;
import sie.domain.Account;
import sie.domain.AccountingPlan;
import sie.domain.Address;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.Entity;
import sie.domain.FinancialYear;
import sie.domain.Generated;
import sie.domain.MetaData;
import sie.domain.Program;
import sie.domain.Transaction;
import sie.domain.Voucher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén
 * <p>
 * This class generates SIE documents populated with fake/fabricated data.
 * <p>
 * Sample:  <code><pre>
 * Document doc = SampleDocumentGenerator.generate();
 * File file = new File(System.getProperty("user.home") + "/"
 * + doc.getMetaData().getCompany().getName() + " - "
 * + LocalDateTime.now().format(Entity.DATE_TIME_FORMAT) + ".SE");
 * Sie4j.fromDocument(doc, file);
 * </pre></code>
 * <p>
 * If you run the main method in this class, the sample SIE data will be written
 * to a file in a created directory ("SIE-test-data") in your home directory,
 * e.g. "[HOME-DIRECTORY]/SIE-test-data/Sverige & Foodservice -
 * 20210211131407.SE"
 * <br>
 * The data is intended for test purposes, any similarities with existing people
 * or companies are unintentional.
 * <p>
 * The source data was generated at
 * <a href="https://fejk.company/">https://fejk.company/</a>
 */
public class SampleDocumentGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, String> SERIES = new HashMap<>();

    static {
        SERIES.put("A", "Huvudserie");
        SERIES.put("K", "Kundfaktura");
        SERIES.put("L", "Leverantörsfaktura");
        SERIES.put("T", "Temp");
    }

    /**
     * Generates a fake SIE Document.
     * <p>
     * Will generate a document populated with fabricated SIE data. Currently
     * only documents of type E4 are supported.
     *
     *
     * @return Document - a fabricated document.
     */
    public static Document generate() {
        return Document.builder()
                .metaData(getMetaData())
                .accountingPlan(AccountingPlan.builder().accounts(createAccounts()).type("BAS 2016, förenklat årsbokslut").apply())
                .vouchers(createVouchers())
                .apply();
    }

    private static MetaData getMetaData() {
        MetaData.Builder builder = MetaData.builder();
        List<SampleCompany> companies = getCompanies();
        List<SamplePerson> people = getPeople();
        SamplePerson generator = people.get(getRandom(people.size()));
        builder.read(Boolean.FALSE)
                .comments("Fabricerat test data i SIE-4E format, skapat " + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".")
                .company(createCompany(companies.get(getRandom(companies.size()))))
                .currency("SEK")
                .financialYears(createYears())
                .taxationYear(Year.now().minusYears(1))
                .program(Program.of("Sie4j", Version.current().toString()))
                .sieType(Document.Type.E4)
                .generated(Generated.of(LocalDate.now(), generator.getName() + " " + generator.getEmail()));
        return builder.apply();
    }

    private static List<SampleCompany> getCompanies() {
        try {
            InputStream stream = SampleDocumentGenerator.class.getResourceAsStream("/companies.json");
            return Arrays.asList(MAPPER.readValue(stream, SampleCompany[].class));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<SamplePerson> getPeople() {
        try {
            InputStream stream = SampleDocumentGenerator.class.getResourceAsStream("/people.json");
            return Arrays.asList(MAPPER.readValue(stream, SamplePerson[].class));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Company createCompany(SampleCompany fc) {
        List<String> lines = getAddressLines(fc.getAddress());
        Address address = Address.builder().contact(fc.getContact())
                .phone(fc.getContactPhone())
                .streetAddress(lines.get(0))
                .postalAddress(lines.get(1) + " " + lines.get(2))
                .apply();
        return Company.builder(fc.getCompanyName())
                .address(address).corporateID(fc.getOrgNum())
                .id(fc.getContactEmail().substring(fc.getContactEmail().indexOf("@") + 1))
                .apply();
    }

    private static List<Account> createAccounts() {
        try {
            InputStream input = SampleDocumentGenerator.class.getResourceAsStream("/accounting-plan.csv");
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            String result = new String(buffer, StandardCharsets.UTF_8);
            return Arrays.stream(result.split("\n"))
                    .map(KeyValue::new)
                    .map(kv -> {
                        return Account.builder(kv.getKey()).label(kv.getValue()).apply();
                    })
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            return Collections.emptyList();
        }
    }

    private static List<FinancialYear> createYears() {
        List<FinancialYear> years = new ArrayList<>();
        int noOfYears = getRandom(2) + 2;
        int year = Year.now().getValue() - 1;
        years.add(FinancialYear.of(0, LocalDate.of(year, Month.JANUARY, 1), LocalDate.of(year, Month.DECEMBER, 31)));
        for (int y = 1; y <= noOfYears; y++) {
            years.add(FinancialYear.of(0 - y, LocalDate.of(year - y, Month.JANUARY, 1), LocalDate.of(year - y, Month.DECEMBER, 31)));
        }
        return years;
    }

    private static List<Voucher> createVouchers() {
        List<KeyValue> series = SERIES.entrySet().stream().map(e -> new KeyValue(e.getKey() + ";" + e.getValue())).collect(Collectors.toList());
        List<Account> accounts = createAccounts();
        List<Voucher> vouchers = new ArrayList<>();
        List<SamplePerson> people = getPeople();
        int currentYear = Year.now().getValue() - 1;
        int noOfMonths = 8 + getRandom(8);
        for (int month = 0; month < noOfMonths; month++) {
            LocalDate monthDate = LocalDate.of(currentYear, Month.DECEMBER, 31).minusMonths(12 - month);
            for (int day = 0; day < monthDate.getMonth().maxLength(); day++) {
                LocalDate dayDate = monthDate.minusDays(monthDate.getMonth().maxLength() - day);
                if (dayDate.getYear() == currentYear && (dayDate.getDayOfWeek().equals(DayOfWeek.MONDAY) || dayDate.getDayOfWeek().equals(DayOfWeek.THURSDAY))) {
                    for (int rand = 0; rand < getRandom(5); rand++) {
                        Integer userId = getRandom(people.size()) + 1;
                        KeyValue kv = series.get(series.size() - 1);
                        SamplePerson person = people.get(userId - 1);
                        Integer num = vouchers.stream().filter(v -> v.getSeries().get().equals(kv.getKey())).collect(Collectors.toList()).size() + 1;
                        Voucher.Builder builder = Voucher.builder();
                        builder.date(dayDate);
                        builder.series(kv.getKey());
                        builder.registrationDate(dayDate.minusDays(getRandom(14)));
                        builder.number(num);
                        builder.signature(userId + " " + person.getName());
                        int sumInt = (getRandom(19) + 10) * (getRandom(13) + 1);
                        int sumDouble = Math.floorDiv(getRandom(7919) + 1, getRandom(211) + 1);
                        BigDecimal amount = new BigDecimal(Double.valueOf(sumInt + "." + sumDouble)).setScale(Entity.SCALE, Entity.ROUNDING_MODE);
                        boolean vat = (dayDate.getDayOfMonth() + 1) % (getRandom(3) + 2) == 0;
                        if (vat) {
                            builder.addTransaction(Transaction.builder().accountNumber("1910")
                                    .amount(amount.setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                    .date(dayDate).apply());
                            builder.addTransaction(Transaction.builder()
                                    .accountNumber("2610").amount(amount.multiply(BigDecimal.valueOf(0.25).negate()).setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                    .date(dayDate).apply());
                            builder.addTransaction(Transaction.builder()
                                    .accountNumber("3010").amount(amount.multiply(BigDecimal.valueOf(0.75).negate()).setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                    .date(dayDate).apply());
                        } else {
                            builder.addTransaction(Transaction.builder().accountNumber(accounts.get(getRandom(accounts.size())).getNumber())
                                    .amount(amount.multiply(BigDecimal.valueOf(0.8)).setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                    .date(dayDate).apply());
                            int noOfTrans = getRandom(2) + 1;
                            for (int i = 0; i < noOfTrans; i++) {
                                builder.addTransaction(Transaction.builder().accountNumber(accounts.get(getRandom(accounts.size())).getNumber())
                                        .amount(amount.multiply(BigDecimal.valueOf(0.3).negate()).setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                        .date(dayDate).apply());
                            }
                            double rest = 1 - (noOfTrans * 0.3);
                            builder.addTransaction(Transaction.builder().accountNumber(accounts.get(getRandom(accounts.size())).getNumber())
                                    .amount(amount.multiply(BigDecimal.valueOf(rest).negate()).setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                    .date(dayDate).apply());
                            builder.addTransaction(Transaction.builder().accountNumber(accounts.get(getRandom(accounts.size())).getNumber())
                                    .amount(amount.multiply(BigDecimal.valueOf(0.2)).setScale(Entity.SCALE, Entity.ROUNDING_MODE))
                                    .date(dayDate).apply());
                        }
                        Voucher v = builder.apply();
                        if (!v.isBalanced()) {
                            builder.addTransaction(Transaction.builder().accountNumber("3740").amount(v.getDiff().negate())
                                    .date(dayDate).apply());
                        }
                        vouchers.add(builder.apply());
                    }
                }
            }
        }
        return vouchers;
    }

    private static List<String> getAddressLines(String input) {
        String street = input.substring(0, input.indexOf("\n") - 1).trim();
        String city = input.substring(input.indexOf("\n") + 1, input.lastIndexOf(",")).trim();
        String zip = input.substring(input.lastIndexOf(",") + 1).trim();
        return Arrays.asList(street, (zip + zip + zip).substring(0, 5), city);
    }

    private static class KeyValue {

        private final String key;
        private final String value;

        public KeyValue(String input) {
            this.key = input.split(";")[0];
            this.value = input.split(";")[1];
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    private static Integer getRandom(Integer max) {
        return new Random().nextInt(max);
    }

    /**
     * Creates a SIE-file with fake data.
     * <p>
     * If you run this method, sample SIE data will be written to a file in a
     * created directory ("SIE-test-data") in your home directory, e.g.
     * "[HOME-DIRECTORY]/SIE-test-data/Sverige & Foodservice -
     * 20210211131407.SE"
     *
     * @param args
     */
    public static void main(String[] args) {
        Document doc = SampleDocumentGenerator.generate();
        File target = new File(System.getProperty("user.home") + "/SIE-test-data/"
                + doc.getMetaData().getCompany().getName() + " - "
                + LocalDateTime.now().format(Entity.DATE_TIME_FORMAT)
                + ".SE");
        target.getParentFile().mkdirs();
        Sie4j.fromDocument(doc, target);
    }
}
