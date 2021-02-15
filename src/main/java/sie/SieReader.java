package sie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 *
 */
class SieReader {

    private SieReader() {
    }

    public static Document read(String input) {
        return SieParser.parse(input);
    }

    public static Document read(InputStream input) {
        return SieParser.parse(input);
    }

    public static Document read(File file) {
        try {
            return read(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new SieException(ex);
        }
    }
}
