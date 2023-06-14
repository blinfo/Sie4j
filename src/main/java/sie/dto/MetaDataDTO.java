package sie.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.*;
import java.time.*;
import java.util.*;
import sie.domain.*;
import sie.io.*;

/**
 *
 * @author Håkan Lidén
 */
@JsonPropertyOrder({"company", "program", "generated", "sieType", "comments",
    "taxationYear", "financialYears", "periodRange", "currency", "read"})
public record MetaDataDTO(
        CompanyDTO company,
        ProgramDTO program,
        GeneratedDTO generated,
        SieTypeDTO sieType,
        String comments,
        @JsonSerialize(using = YearSerializer.class)
        @JsonDeserialize(using = YearDeserializer.class)
        Year taxationYear,
        List<FinancialYearDTO> financialYears,
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate periodRange,
        String currency,
        Boolean read) implements DTO {

    public static MetaDataDTO from(MetaData source) {
        return new MetaDataDTO(
                CompanyDTO.from(source.getCompany()),
                ProgramDTO.from(source.program()),
                GeneratedDTO.from(source.generated()),
                SieTypeDTO.from(source.sieType()),
                source.optComments().orElse(null),
                source.optTaxationYear().orElse(null),
                source.sieType().equals(Document.Type.I4) ? List.of() : source.financialYears().stream().map(FinancialYearDTO::from).toList(),
                source.optPeriodRange().orElse(null),
                source.optCurrency().orElse(null),
                source.isRead());
    }
}
