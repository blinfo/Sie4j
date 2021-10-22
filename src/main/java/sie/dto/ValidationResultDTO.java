package sie.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"document", "logs"})
public class ValidationResultDTO implements DTO {

    private DocumentDTO document;
    private List<SieLogDTO> logs;

    public ValidationResultDTO() {
    }

    public static ValidationResultDTO from(DocumentDTO doc, List<SieLogDTO> logs) {
        ValidationResultDTO dto = new ValidationResultDTO();
        dto.setDocument(doc);
        dto.setLogs(logs);
        return dto;
    }

    public List<SieLogDTO> getLogs() {
        return logs;
    }

    @JsonIgnore
    public List<SieLogDTO> getCriticals() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.CRITICAL.name())).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<SieLogDTO> getWarnings() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.WARNING.name())).collect(Collectors.toList());
    }
    
    @JsonIgnore
    public List<SieLogDTO> getInfos() {
        return logs.stream().filter(log -> log.getLevel().equals(SieLog.Level.INFO.name())).collect(Collectors.toList());
    }

    public void addLog(SieLogDTO log) {
        this.logs.add(log);
    } 

    public void setLogs(List<SieLogDTO> logs) {
        this.logs = logs.stream().distinct().collect(Collectors.toList());
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }
}
