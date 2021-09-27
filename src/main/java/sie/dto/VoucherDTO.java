package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"series", "number", "date", "text", "registrationDate",
    "signature", "balanced", "diff", "transactions"})
public class VoucherDTO {

    private final Voucher source;

    private VoucherDTO(Voucher source) {
        this.source = source;
    }

    public static VoucherDTO from(Voucher source) {
        return new VoucherDTO(source);
    }

    public String getSeries() {
        return source.getSeries().orElse(null);
    }

    public Integer getNumber() {
        return source.getNumber().orElse(null);
    }

    public String getDate() {
        return source.getDate().toString();
    }

    public String getText() {
        return source.getText().orElse(null);
    }

    public String getRegistrationDate() {
        return source.getRegistrationDate().map(LocalDate::toString).orElse(null);
    }

    public String getSignature() {
        return source.getSignature().orElse(null);
    }

    public List<TransactionDTO> getTransactions() {
        return source.getTransactions().stream().map(TransactionDTO::from).collect(Collectors.toList());
    }

    public Boolean isBalanced() {
        return source.isBalanced();
    }

    public BigDecimal getDiff() {
        return source.getDiff();
    }
}
