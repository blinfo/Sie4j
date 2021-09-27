package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Program;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"name", "version"})
public class ProgramDTO implements DTO {

    private String name;
    private String version;

    public ProgramDTO() {
    }

    public ProgramDTO(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public static ProgramDTO from(Program source) {
        return new ProgramDTO(source.getName(), source.getVersion());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
