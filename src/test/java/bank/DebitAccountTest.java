package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void shouldWithdrawWhenBalanceIsSufficient() {

        DebitAccount account = new DebitAccount("101", "Иван", 10000.0);

        boolean result = account.withdraw(8000.0);

        assertTrue(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {

        DebitAccount account = new DebitAccount("101", "Иван", 2000.0);

        boolean result = account.withdraw(3000.0);

        assertFalse(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void shouldNotChangeBalanceWhenWithdrawingNegativeOrZeroAmount() {

        DebitAccount account = new DebitAccount("101", "Иван", 5000.0);

        boolean resultZero = account.withdraw(0.0);
        boolean resultNegative = account.withdraw(-500.0);

        assertFalse(resultZero);
        assertFalse(resultNegative);
        assertEquals(5000.0, account.getBalance());
    }

    @Test
    void shouldIncreaseBalanceOnValidDeposit() {

        DebitAccount account = new DebitAccount("101", "Иван", 1000.0);

        account.deposit(500.0);

        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void shouldNotIncreaseBalanceOnInvalidDeposit() {

        DebitAccount account = new DebitAccount("101", "Иван", 1000.0);

        account.deposit(-100.0);
        account.deposit(0.0);

        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenInitialBalanceIsNegative() {

        assertThrows(IllegalArgumentException.class, () -> new DebitAccount("101", "Иван", -100.0));
    }
}
