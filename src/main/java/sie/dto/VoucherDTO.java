package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Voucher;
import sie.exception.MissingVoucherDateException;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"series", "number", "date", "text", "registrationDate",
    "signature", "balanced", "diff", "transactions"})
public record VoucherDTO(
        String series,
        Integer number,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate date,
        String text,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate registrationDate,
        String signature,
        Boolean balanced,
        BigDecimal diff,
        List<TransactionDTO> transactions) implements DTO {

    public static VoucherDTO from(Voucher source) {
        if (source.date() == null) {
            throw new MissingVoucherDateException();
        }
        return new VoucherDTO(
                source.optSeries().orElse(null),
                source.optNumber().orElse(null),
                source.date(),
                source.optText().orElse(null),
                source.optRegistrationDate().orElse(null),
                source.optSignature().orElse(null),
                source.balanced(),
                source.diff(),
                source.transactions().stream().map(TransactionDTO::from).toList());
    }
}
