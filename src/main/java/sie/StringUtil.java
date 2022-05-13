package sie;

import java.util.List;
import java.util.stream.*;

/**
 *
 * @author Håkan Lidén
 */
class StringUtil {

    public static List<String> getParts(String line) {
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
        return Stream.of(builder.toString().split("\n")).map(String::trim).collect(Collectors.toList());
    }

}
