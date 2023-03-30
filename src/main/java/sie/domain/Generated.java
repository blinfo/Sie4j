package sie.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.*;
import sie.io.LocalDateSerializer;

/**
 *
 * @author Håkan Lidén
 *
 */
public final class Generated implements Entity {

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
    public Optional<String> optLine() {
        return Optional.ofNullable(line);
    }

    public LocalDate date() {
        return date;
    }

    public Optional<String> optSignature() {
        return Optional.ofNullable(signature);
    }

    @Override
    public String toString() {
        return "Generated{" + "date=" + date + ", signature=" + signature + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.date);
        hash = 37 * hash + Objects.hashCode(this.signature);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Generated other = (Generated) obj;
        if (!Objects.equals(this.signature, other.signature)) {
            return false;
        }
        return Objects.equals(this.date, other.date);
    }

}
