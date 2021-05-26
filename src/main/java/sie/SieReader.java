package sie;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import sie.domain.Document;
import sie.domain.Entity;

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
    private final String input;

    private SieReader(String input) {
        this.input = input;
    }

    public static SieReader from(InputStream input) {
        return new SieReader(streamToString(input));
    }
    
    public Document read() {
        DocumentFactory factory = DocumentFactory.from(input);
        return factory.getDocument();
    }
//
//    public static Document read(String input) {
//        DocumentFactory factory = DocumentFactory.from(input);
//        return factory.getDocument();
//    }

    static String streamToString(InputStream input) {
        try {
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            String result = new String(buffer, Entity.CHARSET);
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
            throw new SieException("Could not read source", ex);
        }
    }

    private static boolean isIso8859(String result) {
        return result.contains("�");
    }

    private static Boolean isUtf8(String string) {
        return BOX_DRAWING_CHARS.stream().filter(c -> string.contains(c)).findAny().isPresent();
    }
}
