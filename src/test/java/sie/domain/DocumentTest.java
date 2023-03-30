package sie.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import sie.exception.*;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentTest {

    @Test
    public void test_document_type_get_valid_type() {
        Document.Type expectedType = Document.Type.E2;
        Document.Type result = Document.Type.get("E2");
        assertEquals(expectedType, result);
    }

    @Test
    public void test_document_type_get_invalid_type_throws_exception() {
        String expectedMessage = "F5 is not a valid SIE type";
        InvalidSieTypeException ex = assertThrows(InvalidSieTypeException.class, () -> Document.Type.get("F5"));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void test_document_type_get_null_type_throws_exception() {
        String expectedMessage = "SIE type must not be null";
        SieException ex = assertThrows(SieException.class, () -> Document.Type.get(null));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    public void test_document_type_getNumber() {
        assertTrue(Document.Type.I4.getNumber().equals(4));
    }
}
