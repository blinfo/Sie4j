package sie;

import java.io.InputStream;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén -
 * <a href="mailto:hl@hex.nu">hl@hex.nu</a>
 */
public class Helper {

    static InputStream getSIE(Integer typeNr, char type) {
        return Helper.class.getResourceAsStream("/sample/BLBLOV_SIE" + typeNr + ".S" + type);
    }

    public static Document getDocument(Integer typeNr, char type) {
        return SieParser.parse(getSIE(typeNr, type));
    }
}
