package sie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén
 *
 */
class FaultyCharacters {

    private final static Map<String, String> DEVIATIONS = new HashMap<>();

    static {
        DEVIATIONS.put("α", "à");
        DEVIATIONS.put("ß", "á");
        DEVIATIONS.put("Γ", "â");
        DEVIATIONS.put("µ", "æ");
        DEVIATIONS.put("σ", "å");
        DEVIATIONS.put("Σ", "ä");
        DEVIATIONS.put("τ", "ç");
        DEVIATIONS.put("Φ", "è");
        DEVIATIONS.put("Θ", "è");
        DEVIATIONS.put("Ω", "ê");
        DEVIATIONS.put("δ", "ë");
        DEVIATIONS.put("∩", "ï");
        DEVIATIONS.put("ε", "î");
        DEVIATIONS.put("φ", "í");
        DEVIATIONS.put("∞", "ì");
        DEVIATIONS.put("±", "ñ");
        DEVIATIONS.put("÷", "ö");
        DEVIATIONS.put("⌠", "ô");
        DEVIATIONS.put("≥", "ò");
        DEVIATIONS.put("√", "û");
        DEVIATIONS.put("∙", "ù");
        DEVIATIONS.put("ⁿ", "ü");
        DEVIATIONS.put("\\u00A0", "ÿ");
        DEVIATIONS.put("└", "À");
        DEVIATIONS.put("┴", "Á");
        DEVIATIONS.put("┼", "Å");
        DEVIATIONS.put("─", "Ä");
        DEVIATIONS.put("╞", "Æ");
        DEVIATIONS.put("╟", "Ç");
        DEVIATIONS.put("╔", "É");
        DEVIATIONS.put("╚", "È");
        DEVIATIONS.put("╤", "Ñ");
        DEVIATIONS.put("╓", "Ö");
        DEVIATIONS.put("▄", "Ü");
    }

    private FaultyCharacters() {
    }

    public static List<String> list() {
        return new ArrayList<>(DEVIATIONS.keySet());
    }

    public static String getReplacement(String key) {
        return DEVIATIONS.getOrDefault(key, "?");
    }

    public static Map<String, String> get() {
        return DEVIATIONS;
    }

    public static String getAsConcatenatedString() {
        return list().stream().collect(Collectors.joining());
    }

    public static boolean stringContains(String string) {
        return FaultyCharacters.get().keySet().stream().filter(c -> string.contains(c)).findFirst().isPresent();
    }

    public static String replaceAll(String string) {
        String result = string;
        for (String character : FaultyCharacters.list()) {
            result = result.replaceAll(character, FaultyCharacters.getReplacement(character));
        }
        return result;
    }

}
