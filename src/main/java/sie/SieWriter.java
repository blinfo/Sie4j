package sie;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import sie.domain.Document;
import sie.domain.Entity;

/**
 *
 * @author Håkan Lidén
 *
 */
class SieWriter {

    /**
     * Creates a SIE-string from Document.
     * <p>
     * The string returned is encode with Cp437 Charset as per SIE standard.
     *
     * @param document
     * @return String (Cp437 Encoded)
     */
    public static String write(Document document) {
        return write(document, Entity.CHARSET);
    }


    public static String write(Document document, Charset charset) {
        if (charset.equals(StandardCharsets.UTF_8)) {
            return SieStringBuilder.parse(document);
        }
        return new String(SieStringBuilder.parse(document).getBytes(charset));
    }

    /**
     * Creates a SIE-file.
     * <p>
     * The file returned is encode with Cp437 Charset as per SIE standard.
     *
     * @param document
     * @param target
     * @return File
     */
    public static File write(Document document, File target) {
        return write(document, target, Entity.CHARSET);
    }
    
    public static File write(Document document, File target, Charset charset) {
        try {
            String content = SieStringBuilder.parse(document);
            Files.write(target.toPath(), content.getBytes(charset));
            return target;
        } catch (IOException ex) {
            throw new SieException(ex);
        }
    }
}
