package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoxTest {

    @Test
    void stringBoxWorksWithoutCast() {
        Box<String> box = new Box<>();
        box.set("Java");
        String s = box.get();
        assertEquals("Java", s);
    }

    @Test
    void integerBoxWorksWithoutCast() {
        Box<Integer> box = new Box<>();
        box.set(42);
        Integer n = box.get();
        assertEquals(42, n);
    }
}
