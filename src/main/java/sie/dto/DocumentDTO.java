package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.AccountingDimension;
import sie.domain.AccountingObject;
import sie.domain.AccountingPlan;
import sie.domain.Document;
import sie.domain.MetaData;
import sie.domain.Voucher;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"metaData", "dimensions", "objects", "accounts", "voucherNumberSeries", "vouchers"})
public class DocumentDTO implements DTO {

    private final MetaData metaData;
    private final AccountingPlan accountingPlan;
    private final List<Voucher> vouchers;
    private final List<AccountingDimension> dimensions;
    private final List<AccountingObject> objects;

    private DocumentDTO(MetaData metaData,
            AccountingPlan accountingPlan,
            List<Voucher> vouchers,
            List<AccountingDimension> dimensions,
            List<AccountingObject> objects) {
        this.metaData = metaData;
        this.accountingPlan = accountingPlan;
        this.vouchers = vouchers;
        this.dimensions = dimensions;
        this.objects = objects;
    }

    public static DocumentDTO from(Document document) {
        Builder builder = DocumentDTO.builder()
                .setMetaData(document.getMetaData())
                .setDimensions(document.getDimensions())
                .setObjects(document.getObjects())
                .setVouchers(document.getVouchers());
        document.getAccountingPlan().ifPresent(builder::setAccountingPlan);
        return builder.apply();
    }

    public static Builder builder() {
        return new Builder();
    }

    public MetaDataDTO getMetaData() {
        return MetaDataDTO.from(metaData);
    }

    public AccountingPlanDTO getAccountingPlan() {
        return AccountingPlanDTO.from(accountingPlan);
    }

    public List<AccountingDimensionDTO> getDimensions() {
        return dimensions.stream().sorted().map(AccountingDimensionDTO::from).collect(Collectors.toList());
    }

    public List<AccountingObjectDTO> getObjects() {
        return objects.stream().sorted().map(AccountingObjectDTO::from).collect(Collectors.toList());
    }

    public List<String> getVoucherNumberSeries() {
        return vouchers.stream()
                .map(v -> v.getSeries().orElse(null))
                .filter(s -> s != null)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> getVouchers() {
        return vouchers.stream().sorted().map(VoucherDTO::from).collect(Collectors.toList());
    }

    public static class Builder {

        private MetaData metaData;
        private AccountingPlan accountingPlan;
        private List<AccountingObject> objects;
        private List<AccountingDimension> dimensions;
        private List<Voucher> vouchers;

        private Builder() {
        }

        public Builder setMetaData(MetaData metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder setVouchers(List<Voucher> vouchers) {
            this.vouchers = vouchers;
            return this;
        }

        public Builder setAccountingPlan(AccountingPlan accountingPlan) {
            this.accountingPlan = accountingPlan;
            return this;
        }

        public Builder setDimensions(List<AccountingDimension> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder setObjects(List<AccountingObject> objects) {
            this.objects = objects;
            return this;
        }

        public DocumentDTO apply() {
            return new DocumentDTO(metaData, accountingPlan, vouchers, dimensions, objects);
        }
    }

}
