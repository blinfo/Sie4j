package sie;

import sie.domain.Document;
import sie.dto.DocumentDTO;
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
        Document doc = Deserializer.fromJson(input);
        DocumentValidator validator = DocumentValidator.from(doc);
        return new JsonReader(doc, validator);
    }

    public static DataReader from(DocumentDTO dto) {
        Document doc = Deserializer.fromJson(dto);
        DocumentValidator validator = DocumentValidator.from(doc);
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
