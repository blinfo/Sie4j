package sie;

import sie.domain.Document;
import sie.validate.DocumentValidator;

/**
 *
 * @author Håkan Lidén
 */
class JsonReader implements DataReader {

    private final Document document;
    private final DocumentValidator validator;

    private JsonReader(Document document, DocumentValidator validator) {
        this.document = document;
        this.validator = validator;
    }

    public static DataReader from(byte[] input) {
        return of(input, false);
    }

    public static DataReader of(byte[] input, Boolean checkBalances) {
        Document doc = Deserializer.fromJson(input);
        DocumentValidator validator = DocumentValidator.of(doc, checkBalances);
        return new JsonReader(doc, validator);
    }

    @Override
    public Document read() {
        return document;
    }

    @Override
    public DocumentValidator validate() {
        return validator;
    }
}
