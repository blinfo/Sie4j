package sie;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sie.domain.Document;
import java.io.InputStream;

/**
 *
 * @author Håkan Lidén 
 *
 */
class Serializer {

    private Serializer() {
    }

    /**
     * Converts SIE data to JSON.
     *
     * @param source
     * @return String - JSON object as string.
     */
    public static String asJson(InputStream source) {
        return asJson(SieParser.parse(source));
    }

    /**
     * Converts SIE data to JSON.
     *
     * @param source
     * @return String - JSON object as string.
     */
    public static String asJson(String source) {
        return asJson(SieParser.parse(source));
    }

    /**
     * Converts Document to JSON.
     *
     * @param document
     * @return String - JSON object as string.
     */
    public static String asJson(Document document) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            return mapper.writeValueAsString(document);
        } catch (JsonProcessingException ex) {
            throw new SieException("Could not parse content", ex);
        }
    }
}
