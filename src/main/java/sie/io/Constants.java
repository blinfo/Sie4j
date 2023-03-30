package sie.io;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author hl
 */
public record Constants() {

    static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
}
