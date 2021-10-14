package sie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sie.sample.SampleDocumentGenerator;
import sie.domain.Document;
import sie.dto.DocumentDTO;
import sie.dto.SieLogDTO;
import sie.dto.ValidationResultDTO;
import sie.exception.SieException;
import sie.validate.DocumentValidator;
import sie.validate.SieLog.Level;

/**
 * A java parser for SIE data.
 * <p>
 * This parser will take SIE data and read it to a java domain for ease from use
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

    public static String asJson(Document input) {
        return Serializer.asJson(input);
    }

    public static Document fromJson(InputStream input) {
        return Deserializer.fromJson(input);
    }

    public static Document toValidatedDocument(InputStream input) {
        return SieReader.validator(input).read();
    }

    public static Document toDocument(InputStream input) {
        return SieReader.from(input).read();
    }

    public static Document toDocument(File input) {
        try {
            return toDocument(new FileInputStream(input));
        } catch (FileNotFoundException ex) {
            throw new SieException(ex);
        }
    }

    public static Document toDocument(String jsonInput) {
        return Deserializer.fromJson(jsonInput);
    }

    public static Document toDocument(DocumentDTO dto) {
        return Deserializer.fromJson(dto);
    }

    /**
     * This method produces a UTF-8 String with SIE content.
     *
     * @param input Document
     * @return
     */
    public static String fromDocument(Document input) {
        return fromDocument(input, StandardCharsets.UTF_8);
    }

    /**
     * This method produces a String with SIE content with the charset
     * specified.
     *
     * @param input Document
     * @param charset Charset for the output.
     * @return String
     */
    public static String fromDocument(Document input, Charset charset) {
        return SieWriter.write(input, charset).trim();
    }

    /**
     * This method produces a File with SIE content.
     * <p>
     * The file is the one specified as target. The content will be UTF-8
     * encoded.
     *
     * @param input Document
     * @param target Target file
     * @return
     */
    public static File fromDocument(Document input, File target) {
        return fromDocument(input, target, StandardCharsets.UTF_8);
    }

    /**
     * This method produces a File with SIE content.
     * <p>
     * The file is the one specified as target. The content will be encoded
     * according to the charset parameter.
     *
     * @param input Document
     * @param target Target file
     * @param charset Charset for the file
     * @return
     */
    public static File fromDocument(Document input, File target, Charset charset) {
        return SieWriter.write(input, target, charset);
    }

    public static Document fakeDocument() {
        return SampleDocumentGenerator.generate();
    }

    public static String calculateChecksum(Document input) {
        return Checksum.calculate(input);
    }

    public static String calculateChecksum(InputStream input) {
        return Checksum.calculate(input);
    }

    public static ValidationResultDTO validate(InputStream input) {
        try {
            SieReader reader = SieReader.validator(input);
            List<SieLogDTO> logs = reader.validate().getLogs().stream().map(SieLogDTO::from).collect(Collectors.toList());
            DocumentDTO doc = DocumentDTO.from(reader.read());
            return ValidationResultDTO.from(doc, logs);
        } catch (SieException ex) {
            List<SieLogDTO> logs;
            if (ex.getLocalizedMessage().contains("\n")) {
                logs = Stream.of(ex.getLocalizedMessage().split("\n")).map(s -> {
                    return SieLogDTO.of(Level.CRITICAL.name(), s, ex.getTag().orElse(null), "Sie4j");
                }).collect(Collectors.toList());
            } else {
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), ex.getLocalizedMessage(), ex.getTag().orElse(null), "Sie4j"));
            }
            return ValidationResultDTO.from(null, logs);
        }
    }

    public static ValidationResultDTO validateJson(InputStream input) {
        try {
            Document document = Sie4j.fromJson(input);
            DocumentValidator validator = DocumentValidator.from(document);
            List<SieLogDTO> logs = validator.getLogs().stream().map(SieLogDTO::from).collect(Collectors.toList());
            DocumentDTO docDto = DocumentDTO.from(document);
            return ValidationResultDTO.from(docDto, logs);
        } catch (SieException ex) {
            List<SieLogDTO> logs;
            if (ex.getLocalizedMessage().contains("\n")) {
                logs = Stream.of(ex.getLocalizedMessage().split("\n")).map(s -> {
                    return SieLogDTO.of(Level.CRITICAL.name(), s, ex.getTag().orElse(null), "Sie4j");
                }).collect(Collectors.toList());
            } else {
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), ex.getLocalizedMessage(), ex.getTag().orElse(null), "Sie4j"));
            }
            return ValidationResultDTO.from(null, logs);
        }
    }
}
