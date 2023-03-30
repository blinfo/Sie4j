package sie;

import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.domain.Document;


/**
 *
 * @author Håkan Lidén
 */
public class SerializeDesirializeTest {

    @Test
    public void test_serialize() {
        String jsonFromSie = Sie4j.asJson(getSieSource());
        Document doc = Sie4j.fromJson(jsonFromSie);
        String jsonFromJson = Sie4j.asJson(doc);
        assertEquals(jsonFromSie, jsonFromJson);
    }

    private InputStream getSieSource() {
        return getClass().getResourceAsStream("/sample/BLBLOV_SIE4.SE");
    }
}
