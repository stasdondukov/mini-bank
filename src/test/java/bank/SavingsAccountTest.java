package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    @Test
    void shouldWithdrawWhenBalanceRemainsAboveMinimum() {

        SavingsAccount account = new SavingsAccount("201", "Ольга", 10000.0, 1000.0);

        boolean result = account.withdraw(8500.0);

        assertTrue(result);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {

        SavingsAccount account = new SavingsAccount("201", "Ольга", 1500.0, 1000.0);

        boolean result = account.withdraw(1000.0);

        assertFalse(result);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void shouldAllowWithdrawalExactlyToMinimumBalance() {

        SavingsAccount account = new SavingsAccount("201", "Ольга", 1500.0, 1000.0);

        boolean result = account.withdraw(500.0);

        assertTrue(result);
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void shouldIncreaseBalanceOnDeposit() {

        SavingsAccount account = new SavingsAccount("201", "Ольга", 1000.0, 500.0);

        account.deposit(2000.0);

        assertEquals(3000.0, account.getBalance());
    }
}
