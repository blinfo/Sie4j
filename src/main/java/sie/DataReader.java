
package sie;

import sie.domain.Document;
import sie.validate.DocumentValidator;

/**
 *
 * @author Håkan Lidén
 */
public interface DataReader {

    Document read();
    
    DocumentValidator validate();
}
