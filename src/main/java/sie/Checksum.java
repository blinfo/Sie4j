package sie;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import sie.domain.Document;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
class Checksum {

    private Checksum() {
    }

    public static String calculate(String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(md5.digest());
        } catch (NoSuchAlgorithmException ex) {
            throw new SieException(ex);
        }
    }

    public static String calculate(Document input) {
        return calculate(SieWriter.write(input));
    }

    public static String calculate(InputStream input) {
        return calculate(SieReader.from(input).read());
    }
}
