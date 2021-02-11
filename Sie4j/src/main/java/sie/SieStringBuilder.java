package sie;

import sie.domain.Account;
import sie.domain.Company;
import sie.domain.Document;
import sie.domain.Entity;
import sie.domain.Generated;
import sie.domain.MetaData;
import sie.domain.Program;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Håkan Lidén 
 *
 */
class SieStringBuilder {

    private final Document document;
    private final StringBuilder result;

    private SieStringBuilder(Document document) {
        this.document = document;
        result = new StringBuilder();
    }

    public static String parse(Document document) {
        return new SieStringBuilder(document).asString();
    }

    private String asString() {
        addMetaData();
        addAccountingPlan();
        addVouchers();
        return result.toString();
    }

    private void addVouchers() {
        document.getVouchers().forEach(voucher -> {
            add(Entity.VOUCHER,
                    "\"" + voucher.getSeries().orElse("") + "\"",
                    voucher.getNumber().map(num -> num.toString()).orElse("\"\""),
                    voucher.getDate().format(Entity.DATE_FORMAT),
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
        List<Account> accounts = document.getAccountingPlan().getAccounts();
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
        if (!document.getMetaData().getSieType().equals(Document.Type.I4)) {
            accounts.stream().filter(account -> !account.getOpeningBalances().isEmpty()).forEach(account -> {
                account.getOpeningBalances().forEach(balance -> {
                    add(Entity.OPENING_BALANCE, balance.getYearIndex().toString(), account.getNumber(), balance.getAmount().toString());
                });
            });
            accounts.stream().filter(account -> !account.getClosingBalances().isEmpty()).forEach(account -> {
                account.getClosingBalances().forEach(balance -> {
                    add(Entity.CLOSING_BALANCE, balance.getYearIndex().toString(), account.getNumber(), balance.getAmount().toString());
                });
            });
            accounts.stream().filter(account -> !account.getResults().isEmpty()).forEach(account -> {
                account.getResults().forEach(balance -> {
                    add(Entity.RESULT, balance.getYearIndex().toString(), account.getNumber(), balance.getAmount().toString());
                });
            });
            if (!document.getMetaData().getSieType().equals(Document.Type.E1)) {
                accounts.stream().filter(account -> !account.getPeriodicalBudgets().isEmpty()).forEach(account -> {
                    account.getPeriodicalBudgets().forEach(budg -> {
                        add(Entity.PERIODICAL_BUDGET, budg.getYearIndex().toString(), account.getNumber(), budg.getPeriod().format(Entity.YEAR_MONTH_FORMAT), budg.getAmount().toString());
                    });
                });
            }
        }
    }

    private void addMetaData() {
        MetaData data = document.getMetaData();
        add(Entity.READ, "0");
        addProgram();
        add(Entity.FORMAT, Entity.ENCODING_FORMAT);
        addGenerated();
        add(Entity.TYPE, data.getSieType().getNumber());
        addComment();
        addCompany();
        data.getFinancialYears().forEach(year -> {
            add(Entity.FINANCIAL_YEAR, year.getIndex().toString(), year.getStartDate().format(Entity.DATE_FORMAT), year.getEndDate().format(Entity.DATE_FORMAT));
        });
        data.getTaxationYear().ifPresent(year -> add(Entity.TAXATION_YEAR, year.toString()));
        if (!data.getSieType().equals(Document.Type.E1) && !data.getSieType().equals(Document.Type.I4)) {
            data.getPeriodRange().ifPresent(period -> add(Entity.PERIOD_RANGE, period.format(Entity.DATE_FORMAT)));
        }
        document.getAccountingPlan().getType().ifPresent(type -> add(Entity.ACCOUNTING_PLAN_TYPE, "\"" + type + "\""));
        data.getCurrency().ifPresent(curr -> add(Entity.CURRENCY, curr));
    }

    private void addProgram() {
        Program prog = document.getMetaData().getProgram();
        add(Entity.PROGRAM, "\"" + prog.getName() + "\"", "\"" + prog.getVersion() + "\"");
    }

    private void addGenerated() {
        Generated gen = document.getMetaData().getGenerated();
        add(Entity.GENERATED, gen.getDate().format(Entity.DATE_FORMAT), gen.getSignature().map(sign -> "\"" + sign + "\"").orElse(""));
    }

    private void addComment() {
        document.getMetaData().getComments().ifPresent(comment -> add(Entity.COMMENTS, "\"" + comment + "\""));
    }

    private void addCompany() {
        Company company = document.getMetaData().getCompany();
        company.getType().ifPresent(type -> add(Entity.COMPANY_TYPE, type.name()));
        company.getId().ifPresent(id -> add(Entity.COMPANY_ID, "\"" + id + "\""));
        company.getCorporateID().ifPresent(id -> add(Entity.CORPORATE_ID, id));
        if (!document.getMetaData().getSieType().equals(Document.Type.I4)) {
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
                .append(Stream.of(parts).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" ")))
                .append("\n");
    }
}
