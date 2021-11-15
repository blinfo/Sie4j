package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import sie.domain.Generated;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"date", "signature"})
public class GeneratedDTO implements DTO {

    private String date;
    private String signature;

    public GeneratedDTO() {
    }

    private GeneratedDTO(String date, String signature) {
        this.date = date;
        this.signature = signature;
    }

    public static GeneratedDTO from(Generated source) {
        return new GeneratedDTO(source.getDate().toString(), source.getSignature().map(s -> s == null || s.isBlank() ? null : s).orElse(null));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
