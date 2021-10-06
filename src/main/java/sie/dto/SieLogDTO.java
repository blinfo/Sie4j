package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
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

    public SieLogDTO() {
    }

    private SieLogDTO(String level, String message, String tag, String origin) {
        this.level = level;
        this.message = message;
        this.tag = tag;
        this.origin = origin;
    }

    public static SieLogDTO from(SieLog log) {
        SieLogDTO dto = new SieLogDTO();
        dto.setLevel(log.getLevel().name());
        dto.setMessage(log.getMessage());
        log.getTag().ifPresent(dto::setTag);
        log.getOrigin().ifPresent(dto::setOrigin);
        return dto;
    }
    
    public static SieLogDTO of(String level, String message, String tag, String origin){
        return new SieLogDTO(level, message, tag, origin);
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.level);
        hash = 89 * hash + Objects.hashCode(this.message);
        hash = 89 * hash + Objects.hashCode(this.tag);
        hash = 89 * hash + Objects.hashCode(this.origin);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SieLogDTO other = (SieLogDTO) obj;
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.tag, other.tag)) {
            return false;
        }
        if (!Objects.equals(this.origin, other.origin)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SieLogDTO{" + "level=" + level + ", message=" + message + ", tag=" + tag + ", origin=" + origin + '}';
    }
}
