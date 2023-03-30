package sie;

import sie.dto.SieLogDTO;
import java.io.*;
import java.nio.charset.*;
import java.util.List;
import java.util.stream.*;
import sie.domain.Document;
import sie.dto.*;
import sie.exception.SieException;
import sie.validate.DocumentValidator;
import sie.log.SieLog.Level;

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
 * <th align="left">sie.dto<td>Contains all the dto:s of the entities.
 * <tr>
 * <th align="left">sie.exception<td>Contains a Sie4j specific Exceptions,
 * <tr>
 * <th align="left">sie.io<td>Serializers for java.time.
 * <tr>
 * <th align="left">sie.validate<td>Validators for the Sie4j content.
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
     * Convert SIE data to JSON.
     *
     * @param input InputStream
     * @return
     */
    public static String asJson(InputStream input) {
        return Serializer.asJson(input);
    }

    /**
     * Convert Document to JSON
     *
     * @param input Document
     * @return
     */
    public static String asJson(Document input) {
        return Serializer.asJson(input);
    }

    /**
     * Generate Document from a JSON InputStream.
     *
     * @param input InputStream
     * @return
     */
    public static Document fromJson(InputStream input) {
        return Deserializer.fromJson(SieReader.streamToByteArray(input));
    }

    /**
     * Generate Document from a JSON String.
     *
     * @param input String
     * @return
     */
    public static Document fromJson(String input) {
        return Deserializer.fromJson(input);
    }

    /**
     * Generate Document from a DocumentDTO.
     *
     * @param dto DocumentDTO
     * @return
     */
    public static Document fromJson(DocumentDTO dto) {
        return Deserializer.fromJson(dto);
    }

    /**
     * Create a DataReader from SIE as a byte array.
     *
     * @param input byte[]
     * @return
     */
    public static DataReader readerFromSie(byte[] input) {
        return SieReader.from(input);
    }

    /**
     * Create a DataReader from SIE as a byte array.
     * <p>
     * If the Boolean "checkBalances" is set to true, a check will be made that
     * the opening balances and transactions will match the closing balances.
     * This will only be true if the file contains all transactions for the
     * financial year (and they do indeed balance).
     *
     * @param input byte[]
     * @param checkBalances Boolean
     * @return
     */
    public static DataReader readerFromSieWithBalanceCheckOption(byte[] input, Boolean checkBalances) {
        return SieReader.of(input, checkBalances);
    }

    /**
     * Create a DataReader from JSON as a byte array.
     *
     * @param input byte[]
     * @return
     */
    public static DataReader readerFromJson(byte[] input) {
        return JsonReader.from(input);
    }


    /**
     * Create a DataReader from JSON as a byte array.
     * <p>
     * If the Boolean "checkBalances" is set to true, a check will be made that
     * the opening balances and transactions will match the closing balances.
     * This will only be true if the file contains all transactions for the
     * financial year (and they do indeed balance).
     *
     * @param input byte[]
     * @param checkBalances Boolean
     * @return
     */
    public static DataReader readerFromJsonWithBalanceCheck(byte[] input, Boolean checkBalances) {
        return JsonReader.of(input, checkBalances);
    }

    /**
     * Generate Document from a SIE byte array.
     *
     * @param input byte[]
     * @return
     */
    public static Document fromSie(byte[] input) {
        return SieReader.from(input).read();
    }

    /**
     * Generate Document from a SIE String.
     *
     * @param input String
     * @return
     */
    public static Document fromSie(InputStream input) {
        return fromSie(SieReader.streamToByteArray(input));
    }

    /**
     * Generate Document from a SIE File.
     *
     * @param input File
     * @return
     */
    public static Document fromSie(File input) {
        try {
            return fromSie(new FileInputStream(input));
        } catch (FileNotFoundException ex) {
            throw new SieException(ex);
        }
    }

    /**
     * Produces a UTF-8 String with SIE content.
     *
     * @param input Document
     * @return
     */
    public static String asSie(Document input) {
        return asSie(input, StandardCharsets.UTF_8);
    }

    /**
     * Produces a String with SIE content with the charset
     * specified.
     *
     * @param input Document
     * @param charset Charset for the output.
     * @return String
     */
    public static String asSie(Document input, Charset charset) {
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
    public static File asSie(Document input, File target) {
        return asSie(input, target, StandardCharsets.UTF_8);
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
    public static File asSie(Document input, File target, Charset charset) {
        return SieWriter.write(input, target, charset);
    }

    public static String calculateChecksum(Document input) {
        return Checksum.calculate(input);
    }

    public static ValidationResultDTO validate(DataReader reader) {
        try {
            List<SieLogDTO> logs = reader.validate().getLogs().stream().map(SieLogDTO::from).toList();
            DocumentDTO doc = DocumentDTO.from(reader.read());
            return ValidationResultDTO.from(doc, logs);
        } catch (SieException ex) {
            List<SieLogDTO> logs;
            if (ex.getLocalizedMessage().contains("\n") && !ex.getLocalizedMessage().contains("\n #")) {
                logs = Stream.of(ex.getLocalizedMessage().split("\n")).map(s -> {
                    return SieLogDTO.of(Level.CRITICAL.name(), s, ex.getTag().orElse(null), "Sie4j", null);
                }).toList();
            } else if (ex.getLocalizedMessage().contains("\n #")) {
                String[] parts = ex.getLocalizedMessage().split("\n #");
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), parts[0], ex.getTag().orElse(null), "Sie4j", "#" + parts[1]));
            } else {
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), ex.getLocalizedMessage(), ex.getTag().orElse(null), "Sie4j", null));
            }
            return ValidationResultDTO.from(null, logs);
        }
    }

    public static ValidationResultDTO validate(byte[] input) {
        try {
            DataReader reader = SieReader.from(input);
            List<SieLogDTO> logs = reader.validate().getLogs().stream().map(SieLogDTO::from).toList();
            DocumentDTO doc = DocumentDTO.from(reader.read());
            return ValidationResultDTO.from(doc, logs);
        } catch (SieException ex) {
            List<SieLogDTO> logs;
            if (ex.getLocalizedMessage().contains("\n") && !ex.getLocalizedMessage().contains("\n #")) {
                logs = Stream.of(ex.getLocalizedMessage().split("\n")).map(s -> {
                    return SieLogDTO.of(Level.CRITICAL.name(), s, ex.getTag().orElse(null), "Sie4j", null);
                }).toList();
            } else if (ex.getLocalizedMessage().contains("\n #")) {
                String[] parts = ex.getLocalizedMessage().split("\n #");
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), parts[0], ex.getTag().orElse(null), "Sie4j", "#" + parts[1]));
            } else {
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), ex.getLocalizedMessage(), ex.getTag().orElse(null), "Sie4j", null));
            }
            return ValidationResultDTO.from(null, logs);
        }
    }

    public static ValidationResultDTO validateJson(InputStream input) {
        try {
            Document document = Sie4j.fromJson(input);
            DocumentValidator validator = DocumentValidator.from(document);
            List<SieLogDTO> logs = validator.getLogs().stream().map(SieLogDTO::from).toList();
            DocumentDTO docDto = DocumentDTO.from(document);
            return ValidationResultDTO.from(docDto, logs);
        } catch (SieException ex) {
            List<SieLogDTO> logs;
            if (ex.getLocalizedMessage().contains("\n")) {
                logs = Stream.of(ex.getLocalizedMessage().split("\n")).map(s -> {
                    return SieLogDTO.of(Level.CRITICAL.name(), s, ex.getTag().orElse(null), "Sie4j", null);
                }).toList();
            } else {
                logs = List.of(SieLogDTO.of(Level.CRITICAL.name(), ex.getLocalizedMessage(), ex.getTag().orElse(null), "Sie4j", null));
            }
            return ValidationResultDTO.from(null, logs);
        }
    }
}
