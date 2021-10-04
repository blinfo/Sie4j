package sie.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Håkan Lidén
 *
 */
public class YearSerializer extends JsonSerializer<Year> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy");

    @Override
    public void serialize(Year year, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(year.format(FORMATTER));
    }
}
