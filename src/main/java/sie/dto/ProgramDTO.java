package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Program;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "version"})
public class ProgramDTO implements DTO {

    private final Program source;

    private ProgramDTO(Program source) {
        this.source = source;
    }

    public static ProgramDTO from(Program source) {
        return new ProgramDTO(source);
    }

    public String getName() {
        return source.getName();
    }

    public String getVersion() {
        return source.getVersion();
    }
}
