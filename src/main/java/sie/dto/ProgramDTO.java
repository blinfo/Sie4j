package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Program;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "version"})
public record ProgramDTO(String name, String version) implements DTO {

    public static ProgramDTO from(Program source) {
        return new ProgramDTO(source.getName(), source.getVersion());
    }
}
