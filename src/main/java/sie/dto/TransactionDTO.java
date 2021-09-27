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
@JsonPropertyOrder({"accountNumber", "amount", "text", "date", "signature", "quantity", "costCenterIds", "costBearerIds", "projectIds"})
public class TransactionDTO implements DTO {

    private String accountNumber;
    private BigDecimal amount;
    private String date;
    private String text;
    private String signature;
    private Double quantity;
    private List<String> costCenterIds;
    private List<String> costBearerIds;
    private List<String> projectIds;

    public static TransactionDTO from(Transaction source) {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountNumber(source.getAccountNumber());
        dto.setAmount(source.getAmount());
        source.getDate().map(LocalDate::toString).ifPresent(dto::setDate);
        source.getText().ifPresent(dto::setText);
        source.getSignature().ifPresent(dto::setSignature);
        source.getQuantity().ifPresent(dto::setQuantity);
        dto.setCostCenterIds(source.getCostCentreIds());
        dto.setCostBearerIds(source.getCostBearerIds());
        dto.setProjectIds(source.getProjectIds());
        return dto;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public List<String> getCostCenterIds() {
        return costCenterIds;
    }

    public void setCostCenterIds(List<String> costCenterIds) {
        this.costCenterIds = costCenterIds;
    }

    public List<String> getCostBearerIds() {
        return costBearerIds;
    }

    public void setCostBearerIds(List<String> costBearerIds) {
        this.costBearerIds = costBearerIds;
    }

    public List<String> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<String> projectIds) {
        this.projectIds = projectIds;
    }
}
