package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

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

    private ValidationResultDTO(DocumentDTO document, List<SieLogDTO> logs) {
        this.document = document;
        this.logs = logs;
    }

    public static ValidationResultDTO from(DocumentDTO doc, List<SieLogDTO> logs) {
        return new ValidationResultDTO(doc, logs);
    }

    public List<SieLogDTO> getLogs() {
        return logs;
    }

    public void setLogs(List<SieLogDTO> logs) {
        this.logs = logs;
    }

    public DocumentDTO getDocument() {
        return document;
    }

    public void setDocument(DocumentDTO document) {
        this.document = document;
    }

}
