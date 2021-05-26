package sie;

import java.io.InputStream;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 *
 */
public class Helper {

    protected static InputStream getSIE(Integer typeNr, char type) {
        return Helper.class.getResourceAsStream("/sample/BLBLOV_SIE" + typeNr + ".S" + type);
    }

    protected static Document getDocument(Integer typeNr, char type) {
        return SieReader.from(getSIE(typeNr, type)).read();
    }
}
