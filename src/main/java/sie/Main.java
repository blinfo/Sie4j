package sie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static sie.domain.Entity.CHARSET;
import sie.dto.ValidationResultDTO;

/**
 *
 * @author hakan
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, FileNotFoundException {
        File file = new File("/home/hakan/Hämtningar/302.se");
        ValidationResultDTO result = Sie4j.validate(SieReader.createReader(new FileInputStream(file), true));
        result.getLogs().forEach(System.out::println);
//        Document doc = Sie4j.fromSie(file);
//        Sie4j.asSie(doc, new File("/home/hakan/Hämtningar/302-ny2.se"), CHARSET);
//        Sie4j.asSie(doc, new File("/home/hakan/Hämtningar/302-utf-8.se"), StandardCharsets.UTF_8);
        Sie4j.asSie(Sie4j.fromSie(new File("/home/hakan/Hämtningar/302-utf-8.se")), new File("/home/hakan/Hämtningar/302-cp-437.se"), CHARSET);
    }
}
