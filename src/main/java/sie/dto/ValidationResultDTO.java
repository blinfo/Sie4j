package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"document", "logs"})
public class ValidationResultDTO implements DTO {

    private final DocumentDTO document;
    private final List<SieLogDTO> logs;

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

    public DocumentDTO getDocument() {
        return document;
    }
}
