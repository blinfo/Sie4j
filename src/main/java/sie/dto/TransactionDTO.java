package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import sie.domain.Transaction;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"accountNumber", "amount", "text", "date", "signature", "quantity", "costCenterIds", "costBearerIds", "projeIds"})
public class TransactionDTO implements DTO {

    private final Transaction source;

    private TransactionDTO(Transaction source) {
        this.source = source;
    }

    public static TransactionDTO from(Transaction source) {
        return new TransactionDTO(source);
    }

    public String getAccountNumber() {
        return source.getAccountNumber();
    }

    public BigDecimal getAmount() {
        return source.getAmount();
    }

    public String getDate() {
        return source.getDate().map(LocalDate::toString).orElse(null);
    }

    public String getText() {
        return source.getText().orElse(null);
    }

    public Double getQuantity() {
        return source.getQuantity().orElse(null);
    }

    public String getSignature() {
        return source.getSignature().orElse(null);
    }

    public List<String> getCostCentreIds() {
        return source.getCostCentreIds();
    }

    public List<String> getCostBearerIds() {
        return source.getCostBearerIds();
    }

    public List<String> getProjectIds() {
        return source.getProjectIds();
    }
}
