package sie.validate;

import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentValidator extends AbstractValidator<Document> {

    public DocumentValidator(Document entity) {
        super(entity, entity.getMetaData().getSieType());
    }

    public static DocumentValidator of(Document document) {
        return new DocumentValidator(document);
    }

    @Override
    protected void validate() {
        addErrors(MetaDataValidator.from(entity.getMetaData()).getErrors());
        validateAccountingPlan();
    }

    private void validateAccountingPlan() {
        if (!type.equals(Document.Type.I4) && !entity.getAccountingPlan().isPresent()) {
            addWarning("#KONTO", "Inga konton funna");
        } else {
            entity.getAccountingPlan().ifPresent(plan -> {
                addErrors(AccountingPlanValidator.of(plan, type).getErrors());
            });
        }
    }

}
