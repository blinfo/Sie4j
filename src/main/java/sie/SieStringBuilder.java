package sie;

import java.util.*;
import java.util.stream.*;
import sie.domain.*;
import sie.exception.InvalidDocumentException;

/**
 *
 * @author Håkan Lidén
 *
 */
class SieStringBuilder {

    private final Document document;
    private final Document.Type sieType;
    private final StringBuilder result;

    private SieStringBuilder(Document document) {
        this.document = document;
        this.sieType = document.getMetaData().getSieType();
        result = new StringBuilder();
    }

    public static String parse(Document document) {
        return new SieStringBuilder(document).asString();
    }

    private String asString() {
        addMetaData();
        addAccountingPlan();
        if (sieType.getNumber().equals(4)) {
            addVouchers();
        }
        return result.toString();
    }

    private void addVouchers() {
        document.getVouchers().forEach(voucher -> {
            add(Entity.VOUCHER,
                    "\"" + voucher.getSeries().orElse("") + "\"",
                    voucher.getNumber().map(num -> num.toString()).orElse("\"\""),
                    Optional.ofNullable(voucher.getDate()).map(date -> date.format(Entity.DATE_FORMAT)).orElse("\"\""),
                    "\"" + voucher.getText().orElse("") + "\"",
                    voucher.getRegistrationDate().map(date -> date.format(Entity.DATE_FORMAT)).orElse("\"\""),
                    "\"" + voucher.getSignature().orElse("") + "\"", "\n{");
            voucher.getTransactions().forEach(trans -> {
                add(Entity.TRANSACTION,
                        trans.getAccountNumber(),
                        "{}",
                        trans.getAmount().toString(),
                        trans.getDate().map(date -> date.format(Entity.DATE_FORMAT)).orElse("\"\""),
                        "\"" + trans.getText().orElse("") + "\"",
                        trans.getQuantity().map(q -> q.toString()).orElse(""),
                        trans.getSignature().map(sign -> "\"" + sign + "\"").orElse(""));
            });
            result.append("}\n");
        });
    }

    private void addAccountingPlan() {
        document.getAccountingPlan().ifPresent(ac -> {
            List<Account> accounts = ac.getAccounts();
            accounts.sort(Account::compareTo);
            accounts.forEach(account -> {
                add(Entity.ACCOUNT, account.getNumber(), account.getLabel().map(l -> "\"" + l + "\"").orElse("\"\""));
            });
            accounts.stream().filter(account -> account.getType().isPresent()).forEach(account -> {
                add(Entity.ACCOUNT_TYPE, account.getNumber(), account.getType().get().name());
            });
            accounts.stream().filter(account -> account.getUnit().isPresent()).forEach(account -> {
                add(Entity.ACCOUNT_TYPE, account.getNumber(), "\"" + account.getUnit().get() + "\"");
            });
            accounts.stream().filter(account -> !account.getSruCodes().isEmpty()).forEach(account -> {
                account.getSruCodes().forEach(sru -> add(Entity.SRU, account.getNumber(), sru));
            });
            if (!sieType.equals(Document.Type.I4)) {
                accounts.stream().filter(account -> !account.getOpeningBalances().isEmpty())
                        .flatMap(account -> {
                            return account.getOpeningBalances().stream().map(balance -> new ResultBalance(Entity.OPENING_BALANCE, account.getNumber(), balance));
                        }).sorted().forEach(rb -> add(rb.getTag(), rb.getParts()));
                accounts.stream().filter(account -> !account.getClosingBalances().isEmpty())
                        .flatMap(account -> {
                            return account.getClosingBalances().stream().map(balance -> new ResultBalance(Entity.CLOSING_BALANCE, account.getNumber(), balance));
                        }).sorted().forEach(rb -> add(rb.getTag(), rb.getParts()));
                accounts.stream().filter(account -> !account.getResults().isEmpty())
                        .flatMap(account -> {
                            return account.getResults().stream().map(balance -> new ResultBalance(Entity.RESULT, account.getNumber(), balance));
                        }).sorted().forEach(rb -> add(rb.getTag(), rb.getParts()));
                if (!sieType.equals(Document.Type.E1)) {
                    accounts.stream().filter(account -> !account.getPeriodicalBudgets().isEmpty()).forEach(account -> {
                        account.getPeriodicalBudgets().forEach(budg -> {
                            add(Entity.PERIODICAL_BUDGET, budg.getYearIndex().toString(), budg.getPeriod().format(Entity.YEAR_MONTH_FORMAT), account.getNumber(), budg.getAmount().toString());
                        });
                    });
                }
            }
        });
    }

    private void addMetaData() {
        if (document.getMetaData() == null) {
            throw new InvalidDocumentException("MetaData is missing");
        }
        MetaData data = document.getMetaData();
        add(Entity.READ, "0");
        addProgram();
        add(Entity.FORMAT, Entity.ENCODING_FORMAT);
        addGenerated();
        add(Entity.TYPE, sieType.getNumber().toString());
        addComment();
        addCompany();
        data.getFinancialYears().forEach(year -> {
            add(Entity.FINANCIAL_YEAR, year.getIndex().toString(), year.getStartDate().format(Entity.DATE_FORMAT), year.getEndDate().format(Entity.DATE_FORMAT));
        });
        if (!data.getSieType().equals(Document.Type.E1) && !data.getSieType().equals(Document.Type.I4)) {
            data.getTaxationYear().ifPresent(year -> add(Entity.TAXATION_YEAR, year.toString()));
            data.getPeriodRange().ifPresent(period -> add(Entity.PERIOD_RANGE, period.format(Entity.DATE_FORMAT)));
        }
        if (!sieType.equals(Document.Type.I4)) {
            document.getAccountingPlan().ifPresent(ac -> {
                ac.getType().ifPresent(type -> add(Entity.ACCOUNTING_PLAN_TYPE, "\"" + type + "\""));
            });
        }
        data.getCurrency().ifPresent(curr -> add(Entity.CURRENCY, curr));
    }

    private void addProgram() {
        if (document.getMetaData().getProgram() == null) {
            return;
        }
        Program prog = document.getMetaData().getProgram();
        add(Entity.PROGRAM, "\"" + prog.getName() + "\"", "\"" + prog.getVersion() + "\"");
    }

    private void addGenerated() {
        if (document.getMetaData().getGenerated() == null) {
            return;
        }
        Generated gen = document.getMetaData().getGenerated();
        add(Entity.GENERATED, gen.getDate().format(Entity.DATE_FORMAT), gen.getSignature().map(sign -> "\"" + sign + "\"").orElse(""));
    }

    private void addComment() {
        document.getMetaData().getComments().ifPresent(comment -> add(Entity.COMMENTS, "\"" + comment + "\""));
    }

    private void addCompany() {
        if (document.getMetaData().getCompany() == null) {
            return;
        }
        Company company = document.getMetaData().getCompany();
        company.getType().ifPresent(type -> add(Entity.COMPANY_TYPE, type.name()));
        company.getId().ifPresent(id -> add(Entity.COMPANY_ID, "\"" + id + "\""));
        company.getCorporateID().ifPresent(id -> add(Entity.CORPORATE_ID, id));
        if (!sieType.equals(Document.Type.I4)) {
            company.getSniCode().ifPresent(sni -> add(Entity.COMPANY_SNI_CODE, "\"" + sni + "\""));
        }
        company.getAddress().ifPresent(addr -> add(Entity.ADDRESS, "\"" + addr.getContact() + "\"",
                "\"" + addr.getStreetAddress() + "\"",
                "\"" + addr.getPostalAddress() + "\"",
                "\"" + addr.getPhone() + "\""));
        add(Entity.COMPANY_NAME, "\"" + company.getName() + "\"");
    }

    private void add(String prefix, String... parts) {
        result.append("#").append(prefix).append(" ")
                .append(Stream.of(parts).filter(p -> p != null && !p.isEmpty()).collect(Collectors.joining(" ")))
                .append("\n");
    }

    private static class ResultBalance implements Comparable<ResultBalance> {

        private final String tag;
        private final String number;
        private final Balance balance;

        public ResultBalance(String tag, String number, Balance balance) {
            this.tag = tag;
            this.number = number;
            this.balance = balance;
        }

        public String getTag() {
            return tag;
        }

        public String[] getParts() {
            return new String[]{balance.getYearIndex().toString(), number, balance.getAmount().toString()};
        }

        @Override
        public int compareTo(ResultBalance other) {
            int result = other.balance.getYearIndex().compareTo(this.balance.getYearIndex());
            if (result == 0) {
                return this.number.compareTo(other.number);
            }
            return result;
        }
    }
}
