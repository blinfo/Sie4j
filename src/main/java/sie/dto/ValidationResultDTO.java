package sie.dto;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import java.util.stream.*;
import sie.log.SieLog;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"document", "logs"})
public record ValidationResultDTO(DocumentDTO document, List<SieLogDTO> logs) implements DTO {

    public static ValidationResultDTO from(DocumentDTO doc) {
        return from(doc, List.of());
    }

    public static ValidationResultDTO from(DocumentDTO doc, List<SieLogDTO> logs) {
        ValidationResultDTO dto = new ValidationResultDTO(doc, logs);
        return dto;
    }

    @JsonIgnore
    public List<SieLogDTO> criticals() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.CRITICAL.name())).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<SieLogDTO> warnings() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.WARNING.name())).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<SieLogDTO> infos() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.INFO.name())).collect(Collectors.toList());
    }

    public ValidationResultDTO addLog(SieLogDTO log) {
        List<SieLogDTO> newValue = Stream.concat(logs.stream(), Stream.of(log)).toList();
        return new ValidationResultDTO(document, newValue);
    }

    public ValidationResultDTO setLogs(List<SieLogDTO> logs) {
        return new ValidationResultDTO(document, logs.stream().distinct().toList());
    }
}
