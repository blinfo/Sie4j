package sie;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Håkan Lidén
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(new BigDecimal("0.004").setScale(2, RoundingMode.HALF_UP));
        System.out.println(new BigDecimal("0.005").setScale(2, RoundingMode.HALF_UP));
    }
}
