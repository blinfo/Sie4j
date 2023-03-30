package sie.io;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.Year;

/**
 *
 * @author hl
 */
public class YearDeserializer extends JsonDeserializer<Year> {

    @Override
    public Year deserialize(JsonParser parser, DeserializationContext dc) throws IOException, JacksonException {
        return Year.parse(parser.getText());
    }

}
