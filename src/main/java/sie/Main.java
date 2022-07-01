package sie;

import java.io.FileNotFoundException;
import java.util.List;

/**
 *
 * @author hakan
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, FileNotFoundException {
        List<String> letters = List.of("A", "B", "C");
        System.out.println(letters.indexOf("B"));
    }
}
