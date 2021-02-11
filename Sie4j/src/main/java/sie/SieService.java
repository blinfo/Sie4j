package sie;

import sie.fake.FakeDocumentGenerator;
import sie.domain.Document;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Håkan Lidén
 */
public class SieService {

    public static String asJson(InputStream input) {
        return Serializer.asJson(input);
    }

    public static String asJson(String input) {
        return Serializer.asJson(input);
    }

    public static String asJson(Document input) {
        return Serializer.asJson(input);
    }

    public static Document toDocument(InputStream input) {
        return SieReader.read(input);
    }

    public static Document toDocument(File input) {
        return SieReader.read(input);
    }

    public static Document toDocument(String input) {
        return SieReader.read(input);
    }

    public static String fromDocument(Document input) {
        return SieWriter.write(input);
    }

    public static File fromDocument(Document input, File target) {
        return SieWriter.write(input, target);
    }

    public static Document fakeDocument() {
        return FakeDocumentGenerator.generate();
    }

    public static String calculateChecksum(String input) {
        return Checksum.calculate(input);
    }
    
    public static String calculateChecksum(Document input) {
        return Checksum.calculate(input);
    }
    public static String calculateChecksum(InputStream input) {
        return Checksum.calculate(input);
    }
}
