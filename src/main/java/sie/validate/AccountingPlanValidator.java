package sie.validate;

import sie.domain.AccountingPlan;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
class AccountingPlanValidator extends AbstractValidator<AccountingPlan> {

    private static final String ACCOUNT = "#KONTO",
            ACCOUNTING_PLAN_TYPE = "#KPTYP",
            SRU = "#SRU";

    private AccountingPlanValidator(AccountingPlan entity, Document.Type type) {
        super(entity, type);
    }

    static AccountingPlanValidator of(AccountingPlan entity, Document.Type type) {
        return new AccountingPlanValidator(entity, type);
    }

    @Override
    protected void validate() {
        entity.getType().ifPresent(type -> {
            if (isNullOrBlank(type)) {
                addInfo(ACCOUNTING_PLAN_TYPE, "Kontoplanstypen saknas");
            }
        });
        validate_accounts();
    }

    private void validate_accounts() {
        if (!type.equals(Document.Type.I4) && entity.getAccounts().isEmpty()) {
            addInfo(ACCOUNT, "Konton saknas");
            return;
        }
        entity.getAccounts().forEach(acc -> {
            if (!acc.getSruCodes().isEmpty()) {
                acc.getSruCodes().forEach(sru -> {
                    if (isNullOrBlank(sru)) {
                        addInfo(SRU, "SRU-kod för konto " + acc.getNumber() + " saknas");
                    }
                });
            }
            if (!acc.getNumberAsInteger().isPresent()) {
                addWarning(ACCOUNT, "Kontot har inte ett numeriskt värde: " + acc.getNumber());
            }
        });
    }

}
