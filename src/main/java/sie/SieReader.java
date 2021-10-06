package sie;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import sie.domain.Document;
import sie.domain.Entity;
import sie.exception.SieException;
import sie.validate.DocumentValidator;
import sie.validate.SieLog;

/**
 *
 * @author Håkan Lidén
 *
 */
class SieReader {

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

    private SieReader(String input) {
        this(input, false);
    }

    private SieReader(String input, boolean validate) {
        this.validate = validate;
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
            validator = DocumentValidator.from(document);
            validator.addLogs(factory.getLogs());
        } catch (SieException ex) {
            validator = DocumentValidator.of(ex, DocumentFactory.class);
        }
    }

    public static SieReader validator(InputStream input) {
        return createReader(input, true);

    }

    public static SieReader from(InputStream input) {
        return createReader(input, false);
    }

    private static SieReader createReader(InputStream input, boolean validate) throws SieException {
        if (input == null) {
            throw new SieException("Källan får inte vara null");
        }
        return new SieReader(streamToString(input), validate);
    }

    public Document read() {
        if (!validate && !validator.isValid()) {
            String message = validator.getCriticalErrors().stream().map(SieLog::getMessage).collect(Collectors.joining("\n"));
            throw new SieException(message);
        }
        return document;
    }

    public DocumentValidator validate() {
        return validator;
    }

    static String streamToString(InputStream input) {
        try {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
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
