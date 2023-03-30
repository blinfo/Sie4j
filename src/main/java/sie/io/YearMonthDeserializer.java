package sie.io;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.YearMonth;

/**
 *
 * @author hl
 */
public class YearMonthDeserializer extends JsonDeserializer<YearMonth> {

    @Override
    public YearMonth deserialize(JsonParser parser, DeserializationContext dc) throws IOException, JacksonException {
        return YearMonth.parse(parser.getText(), Constants.YEAR_MONTH_FORMAT);
    }

}
