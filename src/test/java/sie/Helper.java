package sie;

import java.io.InputStream;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Helper {

    protected static byte[] getSIE(Integer typeNr, char type) {
        InputStream stream = Helper.class.getResourceAsStream("/sample/BLBLOV_SIE" + typeNr + ".S" + type);
        return SieReader.streamToByteArray(stream);
    }

    protected static Document getDocument(Integer typeNr, char type) {
        return SieReader.from(getSIE(typeNr, type)).read();
    }
}
