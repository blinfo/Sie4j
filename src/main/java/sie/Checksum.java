package sie;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
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
            return Hex.encodeHexString(Base64.encodeBase64(md5.digest()), false);
        } catch (NoSuchAlgorithmException ex) {
            throw new SieException(ex);
        }
    }

    public static String calculate(Document input) {
        return calculate(SieWriter.write(input));
    }

    public static String calculate(byte[] input) {
        return calculate(SieReader.from(input).read());
    }
}
