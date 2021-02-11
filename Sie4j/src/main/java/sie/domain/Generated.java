package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import sie.io.JsonDateSerializer;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author Håkan Lidén 
 *
 */
public class Generated implements Entity {

    @JsonSerialize(using = JsonDateSerializer.class)
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
