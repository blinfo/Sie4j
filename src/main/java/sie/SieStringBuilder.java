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
        this.sieType = document.metaData().sieType();
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
        document.vouchers().forEach(voucher -> {
            add(Entity.VOUCHER,
                    "\"" + voucher.optSeries().orElse("") + "\"",
                    voucher.optNumber().map(num -> num.toString()).orElse("\"\""),
                    Optional.ofNullable(voucher.date()).map(date -> date.format(Entity.DATE_FORMAT)).orElse("\"\""),
                    "\"" + voucher.optText().orElse("") + "\"",
                    voucher.optRegistrationDate().map(date -> date.format(Entity.DATE_FORMAT)).orElse("\"\""),
                    "\"" + voucher.optSignature().orElse("") + "\"", "\n{");
            voucher.transactions().forEach(trans -> {
                add(Entity.TRANSACTION,
                        trans.accountNumber(),
                        "{}",
                        trans.amount().toString(),
                        trans.optDate().map(date -> date.format(Entity.DATE_FORMAT)).orElse("\"\""),
                        "\"" + trans.optText().orElse("") + "\"",
                        trans.optQuantity().map(q -> q.toString()).orElse(""),
                        trans.getSignature().map(sign -> "\"" + sign + "\"").orElse(""));
            });
            result.append("}\n");
        });
    }

    private void addAccountingPlan() {
        document.optAccountingPlan().ifPresent(ac -> {
            List<Account> accounts = ac.accounts();
            accounts.sort(Account::compareTo);
            accounts.forEach(account -> {
                add(Entity.ACCOUNT, account.number(), account.optLabel().map(l -> "\"" + l + "\"").orElse("\"\""));
            });
            accounts.stream().filter(account -> account.optType().isPresent()).forEach(account -> {
                add(Entity.ACCOUNT_TYPE, account.number(), account.optType().get().name());
            });
            accounts.stream().filter(account -> account.optUnit().isPresent()).forEach(account -> {
                add(Entity.ACCOUNT_TYPE, account.number(), "\"" + account.optUnit().get() + "\"");
            });
            accounts.stream().filter(account -> !account.sruCodes().isEmpty()).forEach(account -> {
                account.sruCodes().forEach(sru -> add(Entity.SRU, account.number(), sru));
            });
            if (!sieType.equals(Document.Type.I4)) {
                accounts.stream().filter(account -> !account.openingBalances().isEmpty())
                        .flatMap(account -> {
                            return account.openingBalances().stream().map(balance -> new ResultBalance(Entity.OPENING_BALANCE, account.number(), balance));
                        }).sorted().forEach(rb -> add(rb.getTag(), rb.getParts()));
                accounts.stream().filter(account -> !account.closingBalances().isEmpty())
                        .flatMap(account -> {
                            return account.closingBalances().stream().map(balance -> new ResultBalance(Entity.CLOSING_BALANCE, account.number(), balance));
                        }).sorted().forEach(rb -> add(rb.getTag(), rb.getParts()));
                accounts.stream().filter(account -> !account.results().isEmpty())
                        .flatMap(account -> {
                            return account.results().stream().map(balance -> new ResultBalance(Entity.RESULT, account.number(), balance));
                        }).sorted().forEach(rb -> add(rb.getTag(), rb.getParts()));
                if (!sieType.equals(Document.Type.E1)) {
                    accounts.stream().filter(account -> !account.getPeriodicalBudgets().isEmpty()).forEach(account -> {
                        account.getPeriodicalBudgets().forEach(budg -> {
                            add(Entity.PERIODICAL_BUDGET, budg.yearIndex().toString(), budg.period().format(Entity.YEAR_MONTH_FORMAT), account.number(), budg.amount().toString());
                        });
                    });
                }
            }
        });
    }

    private void addMetaData() {
        if (document.metaData() == null) {
            throw new InvalidDocumentException("MetaData is missing");
        }
        MetaData data = document.metaData();
        add(Entity.READ, "0");
        addProgram();
        add(Entity.FORMAT, Entity.ENCODING_FORMAT);
        addGenerated();
        add(Entity.TYPE, sieType.getNumber().toString());
        addComment();
        addCompany();
        data.financialYears().forEach(year -> {
            add(Entity.FINANCIAL_YEAR, year.index().toString(), year.startDate().format(Entity.DATE_FORMAT), year.endDate().format(Entity.DATE_FORMAT));
        });
        if (!data.sieType().equals(Document.Type.E1) && !data.sieType().equals(Document.Type.I4)) {
            data.optTaxationYear().ifPresent(year -> add(Entity.TAXATION_YEAR, year.toString()));
            data.optPeriodRange().ifPresent(period -> add(Entity.PERIOD_RANGE, period.format(Entity.DATE_FORMAT)));
        }
        if (!sieType.equals(Document.Type.I4)) {
            document.optAccountingPlan().ifPresent(ac -> {
                ac.optType().ifPresent(type -> add(Entity.ACCOUNTING_PLAN_TYPE, "\"" + type + "\""));
            });
        }
        data.optCurrency().ifPresent(curr -> add(Entity.CURRENCY, curr));
    }

    private void addProgram() {
        if (document.metaData().program() == null) {
            return;
        }
        Program prog = document.metaData().program();
        add(Entity.PROGRAM, "\"" + prog.name() + "\"", "\"" + prog.version() + "\"");
    }

    private void addGenerated() {
        if (document.metaData().generated() == null) {
            return;
        }
        Generated gen = document.metaData().generated();
        add(Entity.GENERATED, gen.date().format(Entity.DATE_FORMAT), gen.optSignature().map(sign -> "\"" + sign + "\"").orElse(""));
    }

    private void addComment() {
        document.metaData().optComments().ifPresent(comment -> add(Entity.COMMENTS, "\"" + comment + "\""));
    }

    private void addCompany() {
        if (document.metaData().getCompany() == null) {
            return;
        }
        Company company = document.metaData().getCompany();
        company.optType().ifPresent(type -> add(Entity.COMPANY_TYPE, type.name()));
        company.optId().ifPresent(id -> add(Entity.COMPANY_ID, "\"" + id + "\""));
        company.optCorporateId().ifPresent(id -> add(Entity.CORPORATE_ID, id));
        if (!sieType.equals(Document.Type.I4)) {
            company.optSniCode().ifPresent(sni -> add(Entity.COMPANY_SNI_CODE, "\"" + sni + "\""));
        }
        company.optAddress().ifPresent(addr -> add(Entity.ADDRESS, "\"" + addr.contact() + "\"",
                "\"" + addr.streetAddress() + "\"",
                "\"" + addr.postalAddress() + "\"",
                "\"" + addr.phone() + "\""));
        add(Entity.COMPANY_NAME, "\"" + company.name() + "\"");
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
            return new String[]{balance.yearIndex().toString(), number, balance.amount().toString()};
        }

        @Override
        public int compareTo(ResultBalance other) {
            int result = other.balance.yearIndex().compareTo(this.balance.yearIndex());
            if (result == 0) {
                return this.number.compareTo(other.number);
            }
            return result;
        }
    }
}
