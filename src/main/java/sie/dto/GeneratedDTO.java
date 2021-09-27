
package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.util.Optional;
import sie.domain.Generated;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"date", "signature"})
public class GeneratedDTO implements DTO {

    private final Generated source;

    private GeneratedDTO(Generated source) {
        this.source = source;
    }
    public static GeneratedDTO from(Generated source) {
        return new GeneratedDTO(source);
    }
    
    public String getDate() {
        return Optional.ofNullable(source.getDate()).map(LocalDate::toString).orElse(null);
    }
    
    public String getSignature() {
        return source.getSignature().map(s -> s.isBlank() ? null : s).orElse(null);
    }
}
