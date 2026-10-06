package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoxTest {

    @Test
    void validCastSucceeds() {
        Box box = new Box();
        box.set("Java");
        String value = (String) box.get();
        assertEquals("Java", value);
    }

    @Test
    void invalidCastThrowsClassCastException() {
        Box box = new Box();
        box.set(123);
        
        assertThrows(ClassCastException.class, () -> {
            String value = (String) box.get();
        });
    }
}
