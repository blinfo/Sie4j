package sie.validate;

import sie.domain.*;

/**
 *
 * @author Håkan Lidén
 */
class AccountingPlanValidator extends AbstractValidator<AccountingPlan> {

    private static final String ACCOUNT = "#KONTO",
            SRU = "#SRU";

    private AccountingPlanValidator(AccountingPlan entity, Document.Type type) {
        super(entity, type);
    }

    static AccountingPlanValidator of(AccountingPlan entity, Document.Type type) {
        return new AccountingPlanValidator(entity, type);
    }

    @Override
    protected void validate() {
        validateAccounts();
    }

    private void validateAccounts() {
        if (!type.equals(Document.Type.I4) && entity.accounts().isEmpty()) {
            addInfo(ACCOUNT, "Konton saknas");
            return;
        }
        entity.accounts().forEach(acc -> {
            if ((type.equals(Document.Type.E1) || type.equals(Document.Type.E2)) && !acc.sruCodes().isEmpty()) {
                acc.sruCodes().forEach(sru -> {
                    if (isNullOrBlank(sru)) {
                        addInfo(SRU, "SRU-kod för konto " + acc.number() + " saknas");
                    }
                });
            }
        });
    }

}
