package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"type", "description"})
public record SieTypeDTO(String type, String description) implements DTO {

    public static SieTypeDTO from(Document.Type type) {
        return new SieTypeDTO(type.name(), type.getDescription());
    }
}
