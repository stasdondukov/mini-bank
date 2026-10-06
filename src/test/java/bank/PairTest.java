package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PairTest {

    @Test
    void stringIntegerPair() {
        Pair<String, Integer> age = new Pair<>("Ivan", 20);
        assertEquals("Ivan", age.key());
        assertEquals(20, age.value());
    }

    @Test
    void accountNumberStringPair() {
        AccountNumber accountNumber = new AccountNumber("1234567890");
        Pair<AccountNumber, String> owner = new Pair<>(accountNumber, "Ivan");
        assertEquals(accountNumber, owner.key());
        assertEquals("Ivan", owner.value());
    }
}
