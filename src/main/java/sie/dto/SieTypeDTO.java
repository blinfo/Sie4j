package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"type", "description"})
public class SieTypeDTO {

    private final Document.Type type;

    private SieTypeDTO(Document.Type type) {
        this.type = type;
    }

    public static SieTypeDTO from(Document.Type type) {
        return new SieTypeDTO(type);
    }

    public String getType() {
        return type.name();
    }

    public String getDescription() {
        return type.getDescription();
    }
}
