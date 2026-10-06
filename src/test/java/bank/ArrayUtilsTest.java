package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilsTest {

    @Test
    void testFirst() {
        Integer[] numbers = {10, 20, 30};
        assertEquals(10, ArrayUtils.first(numbers));
    }

    @Test
    void testFirstThrowsExceptionOnEmptyArray() {
        String[] empty = {};
        assertThrows(IllegalArgumentException.class, () -> ArrayUtils.first(empty));
    }

    @Test
    void testLast() {
        String[] words = {"Apple", "Banana", "Cherry"};
        assertEquals("Cherry", ArrayUtils.last(words));
    }

    @Test
    void testContainsFound() {
        Double[] prices = {1.99, 2.99, 3.99};
        assertTrue(ArrayUtils.contains(prices, 2.99));
    }

    @Test
    void testContainsNotFound() {
        Double[] prices = {1.99, 2.99, 3.99};
        assertFalse(ArrayUtils.contains(prices, 4.99));
    }

    @Test
    void testContainsNull() {
        String[] words = {"A", null, "C"};
        assertTrue(ArrayUtils.contains(words, null));
        assertFalse(ArrayUtils.contains(new String[]{"A", "B"}, null));
    }
}
