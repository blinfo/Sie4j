package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"type", "description"})
public class SieTypeDTO {

    private String type;
    private String description;

    public SieTypeDTO() {
    }

    private SieTypeDTO(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public static SieTypeDTO from(Document.Type type) {
        return new SieTypeDTO(type.name(), type.getDescription());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
