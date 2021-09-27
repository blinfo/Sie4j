package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sie.exception.SieException;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"level", "message", "tag", "origin"})
public class SieLogDTO implements DTO {

    private final SieLog log;

    private SieLogDTO(SieLog log) {
        this.log = log;
    }

    public static SieLogDTO from(SieLog log) {
        return new SieLogDTO(log);
    }

    public String getOrigin() {
        return log.getOrigin().orElse(null);
    }

    public String getLevel() {
        if (log.getLevel() == null) {
            return null;
        }
        return log.getLevel().name();
    }

    public String getTag() {
        return log.getTag().orElse(null);
    }

    public String getMessage() {
        return log.getMessage();
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            throw new SieException(ex);
        }
    }
}
