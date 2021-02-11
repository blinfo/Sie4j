package sie.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
public class JsonYearMonthSerializer extends JsonSerializer<YearMonth> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public void serialize(YearMonth yearMonth, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(yearMonth.format(FORMATTER));
    }
}
