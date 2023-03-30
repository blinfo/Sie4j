package sie.dto;

import com.fasterxml.jackson.annotation.*;
import java.util.List;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"metaData", "dimensions", "objects", "accounts", "voucherNumberSeries", "vouchers", "checksum"})
public record DocumentDTO(MetaDataDTO metaData,
        AccountingPlanDTO accountingPlan,
        List<String> voucherNumberSeries,
        List<VoucherDTO> vouchers,
        List<AccountingDimensionDTO> dimensions,
        List<AccountingObjectDTO> objects,
        String checksum) implements DTO {

    public static DocumentDTO from(Document source) {
        return new DocumentDTO(MetaDataDTO.from(source.metaData()),
                source.optAccountingPlan().map(AccountingPlanDTO::from).orElse(null),
                List.of(),
                source.vouchers().stream().map(VoucherDTO::from).toList(),
                source.dimensions().stream().map(AccountingDimensionDTO::from).toList(),
                source.objects().stream().map(AccountingObjectDTO::from).toList(),
                source.optChecksum().orElse(null));
    }

    public List<String> voucherNumberSeries() {
        return vouchers.stream()
                .map(v -> v.series())
                .filter(s -> s != null)
                .distinct()
                .sorted()
                .toList();
    }
}
