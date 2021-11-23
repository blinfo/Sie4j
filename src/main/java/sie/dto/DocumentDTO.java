package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"metaData", "dimensions", "objects", "accounts", "voucherNumberSeries", "vouchers", "checksum"})
public class DocumentDTO implements DTO {

    private MetaDataDTO metaData;
    private AccountingPlanDTO accountingPlan;
    private List<VoucherDTO> vouchers;
    private List<AccountingDimensionDTO> dimensions;
    private List<AccountingObjectDTO> objects;
    private String checksum;

    public static DocumentDTO from(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setMetaData(MetaDataDTO.from(document.getMetaData()));
        document.getAccountingPlan().map(AccountingPlanDTO::from).ifPresent(dto::setAccountingPlan);
        dto.setVouchers(document.getVouchers().stream().map(VoucherDTO::from).collect(Collectors.toList()));
        dto.setDimensions(document.getDimensions().stream().map(AccountingDimensionDTO::from).collect(Collectors.toList()));
        dto.setObjects(document.getObjects().stream().map(AccountingObjectDTO::from).collect(Collectors.toList()));
        document.getChecksum().ifPresent(dto::setChecksum);
        return dto;
    }

    public MetaDataDTO getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaDataDTO metaData) {
        this.metaData = metaData;
    }

    public AccountingPlanDTO getAccountingPlan() {
        return accountingPlan;
    }

    public void setAccountingPlan(AccountingPlanDTO accountingPlan) {
        this.accountingPlan = accountingPlan;
    }

    public List<VoucherDTO> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<VoucherDTO> vouchers) {
        this.vouchers = vouchers;
    }

    public List<AccountingDimensionDTO> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<AccountingDimensionDTO> dimensions) {
        this.dimensions = dimensions;
    }

    public List<AccountingObjectDTO> getObjects() {
        return objects;
    }

    public void setObjects(List<AccountingObjectDTO> objects) {
        this.objects = objects;
    }

    public List<String> getVoucherNumberSeries() {
        return vouchers.stream()
                .map(v -> v.getSeries())
                .filter(s -> s != null)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void setVoucherNumberSeries(List<String> series) {
        // Read only, Do nothing
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
