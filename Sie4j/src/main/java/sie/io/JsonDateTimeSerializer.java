package sie.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Håkan Lidén - 
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
public class JsonDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
