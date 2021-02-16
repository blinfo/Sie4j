package sie;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import sie.sample.SampleDocumentGenerator;
import sie.domain.Document;

/**
 * A java parser for SIE data.
 * <p>
 * This parser will take SIE data and parse it to a java domain for ease of use
 * in developing situations. The domain fairly accurately represents the data,
 * though it is restructured somewhat for clarity, e.g. all meta-data is
 * collected into the MetaData class.
 * <h3>Packages</h3>
 * <table border="true">
 * <tr>
 * <th align="left">sie<td>This package contains this class (Sie4j) which is
 * used to pars data to and from SIE.
 * <tr>
 * <th align="left">sie.domain<td>Contains all the domain entities and their
 * builders
 * <tr>
 * <th align="left">sie.sample<td>Contains a single class,
 * SampleDocumentGenerator. Use it to generate sample SIE data.
 * <tr>
 * <th align="left">sie.io<td>Serializers for java.time
 * </table>
 * <p>
 * <em>Referenser:</em>
 * <dl>
 * <dt>SIE-Gruppen - <a href="https://sie.se/">https://sie.se/</a>
 * <dd>Information about the SIE standard.
 * <dt>BAS-Intressenternas förening -
 * <a href="https://www.bas.se/">https://www.bas.se/</a>
 * <dd>Information about the different accounting plans typically in use in
 * Sweden.
 * </dl>
 *
 * @author Håkan Lidén
 */
public class Sie4j {

    /**
     * Convert SIE data to JSON
     *
     * @param input
     * @return
     */
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
    public static String fromDocument(Document input, Charset charset) {
        return SieWriter.write(input, charset);
    }

    public static File fromDocument(Document input, File target) {
        return SieWriter.write(input, target);
    }
    public static File fromDocument(Document input, File target, Charset charset) {
        return SieWriter.write(input, target, charset);
    }

    public static Document fakeDocument() {
        return SampleDocumentGenerator.generate();
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
