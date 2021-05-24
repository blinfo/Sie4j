package sie.validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import sie.domain.AccountingPlan;
import sie.domain.Balance;
import sie.domain.Document;
import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 */
class BalanceValidator extends AbstractValidator<Document> {

    private static final String CLOSING_BALANCE = "#UB",
            CLOSING_OBJECT_BALANCE = "#OUB",
            OPENING_BALANCE = "#IB",
            OPENING_OBJECT_BALANCE = "#OIB",
            RESULT = "#RES";

    private BalanceValidator(Document document) {
        super(document, document.getMetaData().getSieType());
    }

    static BalanceValidator from(Document document) {
        return new BalanceValidator(document);
    }

    @Override
    protected void validate() {
        checkForIrregularBalancesAndResults();
        checkBalancesAndResultsAgainstVouchers();
    }

    private void checkBalancesAndResultsAgainstVouchers() {
        if (!type.equals(Document.Type.I4)) {
            entity.getAccountingPlan().ifPresent(plan -> {
                entity.getMetaData().getFinancialYearByIndex(0).ifPresent(fy -> {
                    Integer index = fy.getIndex();
                    checkClosingBalance(plan, index);
                    checkResult(plan, index);
                });
            });
        }
    }

    private void checkClosingBalance(AccountingPlan plan, Integer index) {
        plan.getAccounts().forEach(acc -> {
            acc.getClosingBalanceByYearIndex(index).ifPresent((balance) -> {
                BigDecimal sum = new BigDecimal(entity.getVouchers().stream()
                        .flatMap(voucher -> voucher.getTransactions().stream())
                        .filter(transaction -> transaction.getAccountNumber().equals(acc.getNumber()))
                        .mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum()).setScale(Entity.SCALE, Entity.ROUNDING_MODE);
                // TODO: Kolla upp om inte ingående balans ska läggas till summeringen av verifikationernas rader. // HL 2021-05-24
                sum.add(acc.getOpeningBalanceByYearIndex(index).map(Balance::getAmount).orElse(BigDecimal.ZERO).setScale(Entity.SCALE, Entity.ROUNDING_MODE));
                
                if (!sum.equals(balance.getAmount())) {
                    addFatal(CLOSING_BALANCE, "Utgående balans för konto " + acc.getNumber()
                            + " år " + index + " stämmer inte med summering av verifikationerna."
                                    + " Balans: " + balance.getAmount() + " Summa: " + sum);
                }
            });
        });
    }

    private void checkResult(AccountingPlan plan, Integer index) {
        plan.getAccounts().forEach(acc -> {
            acc.getResultByYearIndex(index).ifPresent((balance) -> {
                BigDecimal sum = new BigDecimal(entity.getVouchers().stream()
                        .flatMap(voucher -> voucher.getTransactions().stream())
                        .filter(transaction -> transaction.getAccountNumber().equals(acc.getNumber()))
                        .mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum()).setScale(Entity.SCALE, Entity.ROUNDING_MODE);
                if (!sum.equals(balance.getAmount())) {
                    addFatal(RESULT, "Resultat för konto " + acc.getNumber()
                            + " år " + index + " stämmer inte med summering av verifikationerna."
                                    + " Resultat: " + balance.getAmount() + " Summa: " + sum);
                }
            });
        });
    }

    private void checkForIrregularBalancesAndResults() {
        if (type.equals(Document.Type.I4)) {
            entity.getAccountingPlan().ifPresent(ac -> {
                ac.getAccounts().stream().flatMap(acc -> acc.getClosingBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(CLOSING_BALANCE, "Filer av typen " + type + " får inte innehålla utgående balans");
                });
                ac.getAccounts().stream().flatMap(acc -> acc.getOpeningBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(OPENING_BALANCE, "Filer av typen " + type + " får inte innehålla ingående balans");
                });
                ac.getAccounts().stream().flatMap(acc -> acc.getResults().stream()).findFirst().ifPresent(b -> {
                    addWarning(RESULT, "Filer av typen " + type + " får inte innehålla resultat");
                });
            });
        }
        if (!type.equals(Document.Type.E3) && !type.equals(Document.Type.E4)) {
            entity.getAccountingPlan().ifPresent(ac -> {
                ac.getAccounts().stream().flatMap(acc -> acc.getObjectClosingBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(CLOSING_OBJECT_BALANCE, "Filer av typen " + type + " får inte innehålla utgåend balans för objekt");
                });
                ac.getAccounts().stream().flatMap(acc -> acc.getObjectOpeningBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(OPENING_OBJECT_BALANCE, "Filer av typen " + type + " får inte innehålla ingående balans för objekt");
                });
            });
        }
    }

}
