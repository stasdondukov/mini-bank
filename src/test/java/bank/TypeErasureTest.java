package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeErasureTest {

    @Test
    void typeErasureExperiment() {
        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        System.out.println(strings.getClass());
        System.out.println(numbers.getClass());
        System.out.println(strings.getClass() == numbers.getClass());

        assertSame(strings.getClass(), numbers.getClass());
        assertTrue(strings.getClass() == numbers.getClass());
    }
}
