package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.Optional;
import sie.io.LocalDateSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Generated implements Entity {

    private final String line;
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate date;
    private final String signature;

    private Generated(String line, LocalDate date, String signature) {
        this.line = line;
        this.date = date;
        this.signature = signature;
    }

    public static Generated of(LocalDate date) {
        return of(null, date, null);
    }

    public static Generated of(String line, LocalDate date) {
        return of(line, date, null);
    }

    public static Generated of(LocalDate date, String signature) {
        return new Generated(null, date, signature);
    }

    public static Generated of(String line, LocalDate date, String signature) {
        return new Generated(line, date, signature);
    }

    @Override
    public Optional<String> getLine() {
        return Optional.ofNullable(line);
    }

    public LocalDate getDate() {
        return date;
    }

    public Optional<String> getSignature() {
        return Optional.ofNullable(signature);
    }

    @Override
    public String toString() {
        return "Generated{" + "date=" + date + ", signature=" + signature + '}';
    }

}
