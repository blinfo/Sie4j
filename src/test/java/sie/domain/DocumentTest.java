package sie.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import sie.exception.InvalidSieTypeException;
import sie.exception.SieException;

/**
 *
 * @author Håkan Lidén
 */
public class DocumentTest {

    @Test
    public void test_document_type_get_valid_type() {
        Document.Type expectedType = Document.Type.E2;
        Document.Type result = Document.Type.get("E2");
        assertEquals("Should be type E2", expectedType, result);
    }

    @Test
    public void test_document_type_get_invalid_type_throws_exception() {
        String expectedMessage = "F5 is not a valid SIE type";
        InvalidSieTypeException ex = assertThrows("Should throw exception", InvalidSieTypeException.class, () -> Document.Type.get("F5"));
        assertEquals("Message", expectedMessage, ex.getMessage());
    }

    @Test
    public void test_document_type_get_null_type_throws_exception() {
        String expectedMessage = "SIE type must not be null";
        SieException ex = assertThrows("Should throw exception", SieException.class, () -> Document.Type.get(null));
        assertEquals("Message", expectedMessage, ex.getMessage());
    }

    @Test
    public void test_document_type_getNumber() {
        assertTrue("Should be 4", Document.Type.I4.getNumber().equals(4));
    }
}
