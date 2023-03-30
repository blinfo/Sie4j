package sie;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import sie.domain.*;
import sie.exception.SieException;
import sie.validate.*;

/**
 *
 * @author Håkan Lidén
 *
 */
class SieReader implements DataReader {

    private static final List<String> BOX_DRAWING_CHARS = Arrays.asList("─", "━", "│",
            "┃", "┄", "┅", "┆", "┇", "┈", "┉", "┊", "┋", "┌", "┍", "┎", "┏", "┐",
            "┑", "┒", "┓", "└", "┕", "┖", "┗", "┘", "┙", "┚", "┛", "├", "┝", "┞",
            "┟", "┠", "┡", "┢", "┣", "┤", "┥", "┦", "┧", "┨", "┩", "┪", "┫", "┬",
            "┭", "┮", "┯", "┰", "┱", "┲", "┳", "┴", "┵", "┶", "┷", "┸", "┹", "┺",
            "┻", "┼", "┽", "┾", "┿", "╀", "╁", "╂", "╃", "╄", "╅", "╆", "╇", "╈",
            "╉", "╊", "╋", "╌", "╍", "╎", "╏", "═", "║", "╒", "╓", "╔", "╕", "╖",
            "╗", "╘", "╙", "╚", "╛", "╜", "╝", "╞", "╟", "╠", "╡", "╢", "╣", "╤",
            "╥", "╦", "╧", "╨", "╩", "╪", "╫", "╬", "╭", "╮", "╯", "╰", "╱", "╲",
            "╳", "╴", "╵", "╶", "╷", "╸", "╹", "╺", "╻", "╼", "╽", "╾", "╿");
    private DocumentValidator validator;
    private DocumentFactory factory;
    private Document document;
    private final boolean validate;
    private final boolean checkBalances;

    private SieReader(String input) {
        this(input, false);
    }

    private SieReader(String input, boolean validate) {
        this(input, validate, false);
    }

    private SieReader(String input, boolean validate, boolean checkBalances) {
        this.validate = validate;
        this.checkBalances = checkBalances;
        init(input);
    }

    private void init(String input) {
        try {
            if (validate) {
                factory = DocumentFactory.validation(input);
            } else {
                factory = DocumentFactory.from(input);
            }
            document = factory.getDocument();
            validator = DocumentValidator.of(document, checkBalances);
            validator.addLogs(factory.getLogs());
        } catch (SieException ex) {
            validator = DocumentValidator.of(ex, DocumentFactory.class);
        }
    }

    /**
     * Will not validate the content.
     *
     * @param input
     * @return
     */
    public static DataReader from(byte[] input) {
        return new SieReader(byteArrayToString(input));
    }

    /**
     * Will always perform a validation of the content.
     * <p>
     * The parameter checkBalances tells whether or not the incoming and
     * outgoing balances should be checked against the vouchers.
     *
     * @param input
     * @param checkBalances
     * @return
     */
    public static DataReader of(byte[] input, Boolean checkBalances) {
        return new SieReader(byteArrayToString(input), true, checkBalances);
    }

    static SieReader createReader(InputStream input, boolean validate) throws SieException {
        byte[] source = streamToByteArray(input);
        return new SieReader(byteArrayToString(source), validate);
    }

    @Override
    public Document read() {
        if (!validate && !validator.isValid()) {
            String message = validator.getCriticalErrors().stream()
                    .map(log -> log.getMessage() + log.getLine().map(l -> "\n " + l).orElse(""))
                    .collect(Collectors.joining("\n"));
            throw new SieException(message, validator.getCriticalErrors().stream()
                    .map(sl -> sl.getTag().orElse(null)).findFirst().orElse(null));
        }
        return document;
    }

    @Override
    public DocumentValidator validate() {
        return validator;
    }

    static byte[] streamToByteArray(InputStream input) {
        try {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            return buffer;
        } catch (NullPointerException ex) {
            throw new SieException("Källan får inte vara null", ex);
        } catch (IOException ex) {
            throw new SieException("Kunde inte läsa källan", ex);
        }
    }

    static String byteArrayToString(byte[] buffer) {
        try {
            String result = new String(buffer, Entity.CHARSET);
            isForTestPurpose(result);
            if (isUtf8(result)) {
                result = new String(buffer, StandardCharsets.UTF_8);
            }
            if (isIso8859(result)) {
                result = new String(buffer, StandardCharsets.ISO_8859_1);
            }
            if (FaultyCharacters.stringContains(result)) {
                result = FaultyCharacters.replaceAll(result);
            }
            return result.replaceAll("\r\n", "\n").replaceAll("\r", "\n").trim();
        } catch (IOException ex) {
            throw new SieException("Kunde inte läsa källan", ex);
        }
    }

    private static void isForTestPurpose(String result) throws IOException {
        if (result.equals("THROW")) {
            throw new IOException();
        }
    }

    private static boolean isIso8859(String result) {
        return result.contains("�");
    }

    private static Boolean isUtf8(String string) {
        return BOX_DRAWING_CHARS.stream().filter(c -> string.contains(c)).findAny().isPresent();
    }
}
