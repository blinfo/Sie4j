package sie;

import java.util.*;
import java.util.stream.*;

/**
 *
 * @author Håkan Lidén
 */
class StringUtil {

    public static List<String> getParts(String line) {
        List<String> result = new ArrayList<>();
        String[] chars = line.replaceAll("\\s+", " ").split("");
        boolean quote = false;
        boolean inlineQuote = false;
        boolean objArray = false;
        StringBuilder builder = new StringBuilder();
        for (String c : chars) {
            if (c.equals("\"")) {
                quote = !quote;
            }
            if (c.equals("\\")) {
                inlineQuote = !inlineQuote;
            }
            if (c.equals("{")) {
                objArray = true;
            }
            if (c.equals("}")) {
                objArray = false;
            }
            if (!quote && !inlineQuote && !objArray && (c.equals(" ") || c.equals("\t"))) {
                builder.append("\n");
            }
            builder.append(c);
        }
        result.addAll(Stream.of(builder.toString().split("\n")).map(String::trim).toList());
        result.add(line);
        return result;
    }

}
