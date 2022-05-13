package sie.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class YearMonthSerializer extends JsonSerializer<YearMonth> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void serialize(YearMonth yearMonth, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(yearMonth.format(FORMATTER));
    }
}
