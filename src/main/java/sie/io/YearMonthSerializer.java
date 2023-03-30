package sie.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.YearMonth;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class YearMonthSerializer extends JsonSerializer<YearMonth> {


    @Override
    public void serialize(YearMonth yearMonth, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(yearMonth.format(Constants.YEAR_MONTH_FORMAT));
    }
}
