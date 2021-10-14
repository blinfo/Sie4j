package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Voucher;
import sie.exception.MissingVoucherDateException;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"series", "number", "date", "text", "registrationDate",
    "signature", "balanced", "diff", "transactions"})
public class VoucherDTO {

    private String series;
    private Integer number;
    private String date;
    private String text;
    private String registrationDate;
    private String signature;
    private Boolean balanced;
    private BigDecimal diff;
    private List<TransactionDTO> transactions;

    public static VoucherDTO from(Voucher source) {
        VoucherDTO dto = new VoucherDTO();
        source.getSeries().ifPresent(dto::setSeries);
        source.getNumber().ifPresent(dto::setNumber);
        if (source.getDate() == null) {
            throw new MissingVoucherDateException();
        }
        dto.setDate(source.getDate().toString());
        source.getText().ifPresent(dto::setText);
        source.getRegistrationDate().map(LocalDate::toString).ifPresent(dto::setRegistrationDate);
        source.getSignature().ifPresent(dto::setSignature);
        dto.setBalanced(source.isBalanced());
        dto.setDiff(dto.getDiff());
        dto.setTransactions(source.getTransactions().stream().map(TransactionDTO::from).collect(Collectors.toList()));
        return dto;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean isBalanced() {
        return balanced;
    }

    public void setBalanced(Boolean balanced) {
        this.balanced = balanced;
    }

    public BigDecimal getDiff() {
        return diff;
    }

    public void setDiff(BigDecimal diff) {
        this.diff = diff;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }
}
