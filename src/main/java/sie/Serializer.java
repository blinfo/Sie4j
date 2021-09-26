package sie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import sie.domain.Document;
import sie.dto.DocumentDTO;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
class Serializer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String asJson(InputStream source) {
        return asJson(SieReader.from(source).read());
    }

    public static String asJson(Document document) {
        try {
            DocumentDTO dto = DocumentDTO.from(document);
            return MAPPER.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            throw new SieException(ex);
        }
    }
}
