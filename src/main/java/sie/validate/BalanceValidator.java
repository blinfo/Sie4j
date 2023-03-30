package sie.validate;

import java.math.BigDecimal;
import sie.domain.*;

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
        super(document, document.metaData().sieType());
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
        if (type.equals(Document.Type.E4)) {
            entity.optAccountingPlan().ifPresent(plan -> {
                entity.metaData().optFinancialYearByIndex(0).ifPresent(fy -> {
                    Integer index = fy.index();
                    checkClosingBalance(plan, index);
                    checkResult(plan, index);
                });
            });
        }
    }

    private void checkClosingBalance(AccountingPlan plan, Integer index) {
        plan.accounts().forEach(acc -> {
            acc.optClosingBalanceByYearIndex(index).ifPresent((balance) -> {
                BigDecimal sumOfTransactions = new BigDecimal(entity.vouchers().parallelStream()
                        .flatMap(voucher -> voucher.transactions().parallelStream())
                        .filter(transaction -> transaction.accountNumber().equals(acc.number()))
                        .mapToDouble(transaction -> transaction.amount().doubleValue()).sum()).setScale(Entity.SCALE, Entity.ROUNDING_MODE);
                BigDecimal sumWithOpeningBalance = sumOfTransactions.add(acc.optOpeningBalanceByYearIndex(index).map(Balance::amount).orElse(BigDecimal.ZERO).setScale(Entity.SCALE, Entity.ROUNDING_MODE));
                if (!sumWithOpeningBalance.equals(balance.amount())) {
                    addWarning(CLOSING_BALANCE, "Utgående balans för konto " + acc.number()
                            + " år " + index + " stämmer inte med summering av ingående balans och verifikationerna"
                            + "\n Balans: " + balance.amount() + " Summa: " + sumWithOpeningBalance + balance.optLine().map(l -> "\n " + l).orElse(""));
                }
            });
        });
    }

    private void checkResult(AccountingPlan plan, Integer index) {
        plan.accounts().forEach(acc -> {
            acc.optResultByYearIndex(index).ifPresent((balance) -> {
                BigDecimal sumOfTransactions = new BigDecimal(entity.vouchers().parallelStream()
                        .flatMap(voucher -> voucher.transactions().parallelStream())
                        .filter(transaction -> transaction.accountNumber().equals(acc.number()))
                        .mapToDouble(transaction -> transaction.amount().doubleValue()).sum()).setScale(Entity.SCALE, Entity.ROUNDING_MODE);
                if (!sumOfTransactions.equals(balance.amount())) {
                    addWarning(RESULT, "Resultat för konto " + acc.number()
                            + " år " + index + " stämmer inte med summering av verifikationerna"
                            + "\n Resultat: " + balance.amount() + " Summa: " + sumOfTransactions + balance.optLine().map(l -> "\n " + l).orElse(""));
                }
            });
        });
    }

    private void checkForIrregularBalancesAndResults() {
        if (type.equals(Document.Type.I4)) {
            entity.optAccountingPlan().ifPresent(ac -> {
                ac.accounts().stream().flatMap(acc -> acc.closingBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(CLOSING_BALANCE, "Filer av typen " + type + " får inte innehålla utgående balans" + b.optLine().map(l -> "\n " + l).orElse(""));
                });
                ac.accounts().stream().flatMap(acc -> acc.openingBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(OPENING_BALANCE, "Filer av typen " + type + " får inte innehålla ingående balans" + b.optLine().map(l -> "\n " + l).orElse(""));
                });
                ac.accounts().stream().flatMap(acc -> acc.results().stream()).findFirst().ifPresent(b -> {
                    addWarning(RESULT, "Filer av typen " + type + " får inte innehålla resultat" + b.optLine().map(l -> "\n " + l).orElse(""));
                });
            });
        }
        if (!type.equals(Document.Type.E3) && !type.equals(Document.Type.E4)) {
            entity.optAccountingPlan().ifPresent(ac -> {
                ac.accounts().stream().flatMap(acc -> acc.optObjectClosingBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(CLOSING_OBJECT_BALANCE, "Filer av typen " + type + " får inte innehålla utgåend balans för objekt" + b.optLine().map(l -> "\n " + l).orElse(""));
                });
                ac.accounts().stream().flatMap(acc -> acc.objectOpeningBalances().stream()).findFirst().ifPresent(b -> {
                    addWarning(OPENING_OBJECT_BALANCE, "Filer av typen " + type + " får inte innehålla ingående balans för objekt" + b.optLine().map(l -> "\n " + l).orElse(""));
                });
            });
        }
    }
}
