package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountNumberTest {

    @Test
    void shouldCreateAccountNumberWithValidTenDigits() {
        AccountNumber number = new AccountNumber("1234567890");
        assertEquals("1234567890", number.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(""));
    }

    @Test
    void shouldThrowExceptionWhenLengthIsLessThanTen() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123456789"));
    }

    @Test
    void shouldThrowExceptionWhenLengthIsGreaterThanTen() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345678901"));
    }

    @Test
    void shouldThrowExceptionWhenContainsNonDigits() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123456789a"));
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("abcdefghij"));
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123-456-78"));
    }
}
