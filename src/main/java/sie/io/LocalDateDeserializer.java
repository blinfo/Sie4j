package sie.io;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.LocalDate;

/**
 *
 * @author hl
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext dc) throws IOException, JacksonException {
        return LocalDate.parse(parser.getText());
    }
}
