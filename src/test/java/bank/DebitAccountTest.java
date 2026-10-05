package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void initialBalanceIsPreserved() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 5000.0);
        assertEquals(5000.0, account.getBalance());
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 1000.0);
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void zeroDepositThrowsException() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 1000.0);
        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(0.0)
        );
    }

    @Test
    void negativeDepositThrowsException() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 1000.0);
        assertThrows(
            InvalidAmountException.class,
            () -> account.deposit(-200.0)
        );
    }

    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 10000.0);
        boolean result = account.withdraw(8000.0);
        assertTrue(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 2000.0);
        boolean result = account.withdraw(3000.0);
        assertFalse(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void zeroWithdrawalIsForbidden() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 5000.0);
        boolean result = account.withdraw(0.0);
        assertFalse(result);
        assertEquals(5000.0, account.getBalance());
    }

    @Test
    void negativeWithdrawalIsForbidden() {
        DebitAccount account = new DebitAccount("0000000101", "Иван", 5000.0);
        boolean result = account.withdraw(-500.0);
        assertFalse(result);
        assertEquals(5000.0, account.getBalance());
    }
}



