package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    @Test
    void canWithdrawWhenMinimumBalanceIsPreserved() {
        SavingsAccount account = new SavingsAccount("0000000201", "Ольга", 10000.0, 1000.0);
        account.withdraw(8500.0);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount("0000000201", "Ольга", 1500.0, 1000.0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1000.0));
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void balanceDoesNotChangeOnFailedWithdrawal() {
        SavingsAccount account = new SavingsAccount("0000000201", "Ольга", 1500.0, 1000.0);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(2000.0));
        assertEquals(1500.0, account.getBalance());
    }
}


