package sie.domain;

import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Håkan Lidén 
 *
 */
public interface Entity {
    public static final String ACCOUNT = "KONTO";
    public static final String ACCOUNT_TYPE = "KTYP";
    public static final String ACCOUNTING_PLAN_TYPE = "KPTYP";
    public static final String ADDRESS = "ADRESS";
    public static final String CLOSING_BALANCE = "UB";
    public static final String COMMENTS = "PROSA";
    public static final String COMPANY_ID = "FNR";
    public static final String COMPANY_NAME = "FNAMN";
    public static final String COMPANY_SNI_CODE = "BKOD";
    public static final String COMPANY_TYPE = "FTYP";
    public static final String CORPORATE_ID = "ORGNR";
    public static final String CURRENCY = "VALUTA";
    public static final String DIMENSION = "DIM";
    public static final String FINANCIAL_YEAR = "RAR";
    public static final String FORMAT = "FORMAT";
    public static final String GENERATED = "GEN";
    public static final String OBJECT = "OBJEKT";
    public static final String OBJECT_CLOSING_BALANCE = "OUB";
    public static final String OBJECT_OPENING_BALANCE = "OIB";
    public static final String OPENING_BALANCE = "IB";
    public static final String PERIOD_RANGE = "OMFATTN";
    public static final String PERIODICAL_BALANCE = "PSALDO";
    public static final String PERIODICAL_BUDGET = "PBUDGET";
    public static final String PROGRAM = "PROGRAM";
    public static final String READ = "FLAGGA";
    public static final String RESULT = "RES";
    public static final String SRU = "SRU";
    public static final String TAXATION_YEAR = "TAXAR";
    public static final String TRANSACTION = "TRANS";
    public static final String TYPE = "SIETYP";
    public static final String UNIT = "ENHET";
    public static final String VOUCHER = "VER";

    public static final Integer SCALE = 2; 
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN; 
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");
    public static final String ENCODING_FORMAT = "PC8";
    public static final Charset CHARSET = Charset.forName("Cp437");
    public static final String DEFAULT_CURRENCY = "SEK";

}
