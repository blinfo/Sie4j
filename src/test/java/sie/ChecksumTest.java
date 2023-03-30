package sie;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.domain.Document;

/**
 *
 * @author Håkan Lidén
 */
public class ChecksumTest {

    private static final Document DOCUMENT = Sie4j.fromSie(ChecksumTest.class.getResourceAsStream("/sample/BLBLOV_SIE1.SE"));

    private static byte[] getStream() {
        return SieReader.streamToByteArray(ChecksumTest.class.getResourceAsStream("/sample/BLBLOV_SIE1.SE"));
    }

    public ChecksumTest() {
    }

    @Test
    public void testCalculate_String() {
        String input = "Sie4j";
        String expResult = "6F326467746F3131616E45332F364E4577692F5856773D3D";
        String result = Checksum.calculate(input);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalculate_Document() {
        String expResult = "344D614846547151326F2F466962684E6C6E683179413D3D";
        assertTrue(DOCUMENT.optChecksum().isPresent());
        assertEquals(expResult, DOCUMENT.optChecksum().get());
        assertEquals(expResult, Checksum.calculate(DOCUMENT));
        assertEquals(expResult, Checksum.calculate(getStream()));
    }

    @Test
    public void test1_that_calculate_with_sources_from_both_sie_and_json_matches() {
        Document jsonDoc = Sie4j.fromJson(getClass().getResourceAsStream("/sample/json/BLBLOV_SIE4_UTF_8_SI.json"));
        Document sieDoc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/BLBLOV_SIE4_UTF_8.SI"));
        String jsonChecksum = Sie4j.calculateChecksum(jsonDoc);
        String sieChecksum = Sie4j.calculateChecksum(sieDoc);
        assertEquals(sieChecksum, jsonChecksum);
    }

    @Test
    public void test2_that_calculate_with_sources_from_both_sie_and_json_matches() {
        String jsonString = Sie4j.asJson(getClass().getResourceAsStream("/sample/Arousells_Visning_AB.SE"));
        Document jsonDoc = Sie4j.fromJson(jsonString);
        Document sieDoc = Sie4j.fromSie(getClass().getResourceAsStream("/sample/Arousells_Visning_AB.SE"));
        String jsonChecksum = Sie4j.calculateChecksum(jsonDoc);
        String sieChecksum = Sie4j.calculateChecksum(sieDoc);
        assertEquals(sieChecksum, jsonChecksum);
    }
}
