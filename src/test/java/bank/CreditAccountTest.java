package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    @Test
    void shouldAllowWithdrawalIntoNegativeBalanceWithinLimit() {

        CreditAccount account = new CreditAccount("301", "Алексей", 1000.0, 5000.0);

        boolean result = account.withdraw(4000.0);

        assertTrue(result);
        assertEquals(-3000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawBeyondCreditLimit() {

        CreditAccount account = new CreditAccount("301", "Алексей", 1000.0, 5000.0);
        account.withdraw(4000.0); 

        boolean result = account.withdraw(3000.0); 

        assertFalse(result);
        assertEquals(-3000.0, account.getBalance());
    }

    @Test
    void shouldAllowWithdrawalExactlyToNegativeCreditLimit() {

        CreditAccount account = new CreditAccount("301", "Алексей", 1000.0, 5000.0);

        boolean result = account.withdraw(6000.0); 

        assertTrue(result);
        assertEquals(-5000.0, account.getBalance());
    }

    @Test
    void shouldIncreaseBalanceOnDepositWhenNegative() {

        CreditAccount account = new CreditAccount("301", "Алексей", 1000.0, 5000.0);
        account.withdraw(4000.0); 

        account.deposit(2000.0);

        assertEquals(-1000.0, account.getBalance());
    }
}
