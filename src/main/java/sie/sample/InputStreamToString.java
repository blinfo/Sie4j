package sie.sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 *
 * @author Håkan Lidén
 */
class InputStreamToString {

    private final InputStream input;
    private final Charset charset;

    private InputStreamToString(InputStream input, Charset charset) {
        this.input = input;
        this.charset = charset;
    }

    public static String from(InputStream input) {
        return of(input, StandardCharsets.UTF_8);
    }

    public static String of(InputStream input, Charset charset) {
        return new InputStreamToString(input, charset).convert();
    }

    private String convert() {
        return new BufferedReader(new InputStreamReader(input, charset))
                .lines()
                .collect(Collectors.joining("\n"));
    }

}
