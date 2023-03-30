package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.time.LocalDate;
import sie.domain.Generated;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"date", "signature"})
public record GeneratedDTO(@JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate date,
        String signature) implements DTO {

    public static GeneratedDTO from(Generated source) {
        return new GeneratedDTO(source.getDate(), source.getSignature().map(s -> s == null || s.isBlank() ? null : s).orElse(null));
    }
}
