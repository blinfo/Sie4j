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

    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate date;
    private final String signature;

    private Generated(LocalDate date, String signature) {
        this.date = date;
        this.signature = signature;
    }

    public static Generated from(LocalDate date) {
        return of(date, null);
    }

    public static Generated of(LocalDate date, String signature) {
        return new Generated(date, signature);
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
