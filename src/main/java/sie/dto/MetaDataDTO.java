package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.MetaData;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"company", "program", "generated", "type", "comments", "taxationYear", "financialYears", "periodRange", "currency", "read"})
public class MetaDataDTO implements DTO {

    private final MetaData source;

    private MetaDataDTO(MetaData source) {
        this.source = source;
    }

    public static MetaDataDTO from(MetaData source) {
        return new MetaDataDTO(source);
    }

    public CompanyDTO getCompany() {
        if (source.getCompany() == null) {
            return null;
        }
        return CompanyDTO.from(source.getCompany());
    }

    public ProgramDTO getProgram() {
        if (source.getProgram() == null) {
            return null;
        }
        return ProgramDTO.from(source.getProgram());
    }

    public GeneratedDTO getGenerated() {
        if (source.getGenerated() == null) {
            return null;
        }
        return GeneratedDTO.from(source.getGenerated());
    }

    public SieTypeDTO getSieType() {
        if (source.getSieType() == null) {
            return null;
        }
        return SieTypeDTO.from(source.getSieType());
    }

    public String getComments() {
        return source.getComments().orElse(null);
    }

    public String getTaxationYear() {
        return source.getTaxationYear().map(Year::toString).orElse(null);
    }

    public List<FinancialYearDTO> getFinancialYears() {
        return source.getFinancialYears().stream().map(FinancialYearDTO::from).collect(Collectors.toList());
    }

    public String getPeriodRange() {
        return source.getPeriodRange().map(LocalDate::toString).orElse(null);
    }

    public String getCurrency() {
        return source.getCurrency().orElse(null);
    }

    public Boolean isRead() {
        return source.isRead();
    }
}
