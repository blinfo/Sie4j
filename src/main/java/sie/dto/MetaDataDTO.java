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
@JsonPropertyOrder({"company", "program", "generated", "sieType", "comments",
    "taxationYear", "financialYears", "periodRange", "currency", "read"})
public class MetaDataDTO implements DTO {

    private CompanyDTO company;
    private ProgramDTO program;
    private GeneratedDTO generated;
    private SieTypeDTO sieType;
    private String comments;
    private String taxationYear;
    private List<FinancialYearDTO> financialYears;
    private String periodRange;
    private String currency;
    private Boolean read;

    public static MetaDataDTO from(MetaData source) {
        MetaDataDTO dto = new MetaDataDTO();
        dto.setCompany(CompanyDTO.from(source.getCompany()));
        dto.setProgram(ProgramDTO.from(source.getProgram()));
        dto.setGenerated(GeneratedDTO.from(source.getGenerated()));
        dto.setSieType(SieTypeDTO.from(source.getSieType()));
        source.getComments().ifPresent(dto::setComments);
        source.getTaxationYear().map(Year::toString).ifPresent(dto::setTaxationYear);
        dto.setFinancialYears(source.getFinancialYears().stream().map(FinancialYearDTO::from).collect(Collectors.toList()));
        source.getPeriodRange().map(LocalDate::toString).ifPresent(dto::setPeriodRange);
        source.getCurrency().ifPresent(dto::setCurrency);
        dto.setRead(source.isRead());
        return dto;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public ProgramDTO getProgram() {
        return program;
    }

    public void setProgram(ProgramDTO program) {
        this.program = program;
    }

    public GeneratedDTO getGenerated() {
        return generated;
    }

    public void setGenerated(GeneratedDTO generated) {
        this.generated = generated;
    }

    public SieTypeDTO getSieType() {
        return sieType;
    }

    public void setSieType(SieTypeDTO sieType) {
        this.sieType = sieType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTaxationYear() {
        return taxationYear;
    }

    public void setTaxationYear(String taxationYear) {
        this.taxationYear = taxationYear;
    }

    public List<FinancialYearDTO> getFinancialYears() {
        return financialYears;
    }

    public void setFinancialYears(List<FinancialYearDTO> financialYears) {
        this.financialYears = financialYears;
    }

    public String getPeriodRange() {
        return periodRange;
    }

    public void setPeriodRange(String periodRange) {
        this.periodRange = periodRange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
