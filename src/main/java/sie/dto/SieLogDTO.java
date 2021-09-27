package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"level", "message", "tag", "origin"})
public class SieLogDTO implements DTO {

    private String level;
    private String message;
    private String tag;
    private String origin;

    public static SieLogDTO from(SieLog log) {
        SieLogDTO dto = new SieLogDTO();
        dto.setLevel(log.getLevel().name());
        dto.setMessage(log.getMessage());
        log.getTag().ifPresent(dto::setTag);
        log.getOrigin().ifPresent(dto::setOrigin);
        return dto;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
