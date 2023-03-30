package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import sie.domain.Transaction;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"accountNumber", "amount", "text", "date", "signature", "quantity", "costCenterIds", "costBearerIds", "projectIds"})
public record TransactionDTO(
        String accountNumber,
        BigDecimal amount,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate date,
        String text,
        String signature,
        Double quantity,
        List<String> costCenterIds,
        List<String> costBearerIds,
        List<String> projectIds) implements DTO {

    public static TransactionDTO from(Transaction source) {
        return new TransactionDTO(
                source.getAccountNumber(),
                source.getAmount(),
                source.getDate().orElse(null),
                source.getText().orElse(null),
                source.getSignature().orElse(null),
                source.getQuantity().orElse(null),
                source.getCostCentreIds(),
                source.getCostBearerIds(),
                source.getProjectIds());
    }
}
