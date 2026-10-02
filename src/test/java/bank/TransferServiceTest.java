package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    private static class DummyNotificationService implements NotificationService {
        @Override
        public void notify(String message) {
        }
    }

    @Test
    void successfulTransferChangesBothBalances() {
        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 3000.0);

        assertTrue(result);
        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeAnyBalance() {
        BankAccount from = new DebitAccount("1", "A", 1000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 3000.0);

        assertFalse(result);
        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferNegativeAmount() {
        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, -500.0);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferZeroAmount() {
        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 0.0);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferToSameAccount() {
        BankAccount account = new DebitAccount("1", "A", 10000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(account, account, 3000.0);

        assertFalse(result);
        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void commissionIsDeductedFromSender() {
        BankAccount from = new DebitAccount("1", "A", 11000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new PercentCommission(1.0), new DummyNotificationService());

        boolean result = service.transfer(from, to, 10000.0);

        assertTrue(result);
        assertEquals(900.0, from.getBalance());
    }

    @Test
    void receiverGetsExactlyTransferAmount() {
        BankAccount from = new DebitAccount("1", "A", 11000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new PercentCommission(1.0), new DummyNotificationService());

        boolean result = service.transfer(from, to, 10000.0);

        assertTrue(result);
        assertEquals(12000.0, to.getBalance());
    }

    @Test
    void transferFailsWhenInsufficientFundsForAmountWithCommission() {
        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TransferService service = new TransferService(new PercentCommission(1.0), new DummyNotificationService());

        boolean result = service.transfer(from, to, 10000.0);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void transferFromDebitAccountToDebitAccount() {
        BankAccount from = new DebitAccount("1", "A", 5000.0);
        BankAccount to = new DebitAccount("2", "B", 1000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 2000.0);

        assertTrue(result);
        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromDebitAccountToSavingsAccount() {
        BankAccount from = new DebitAccount("1", "A", 5000.0);
        BankAccount to = new SavingsAccount("2", "B", 2000.0, 1000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 2000.0);

        assertTrue(result);
        assertEquals(3000.0, from.getBalance());
        assertEquals(4000.0, to.getBalance());
    }

    @Test
    void transferFromCreditAccountToDebitAccount() {
        BankAccount from = new CreditAccount("1", "A", 500.0, 3000.0);
        BankAccount to = new DebitAccount("2", "B", 1000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 2000.0);

        assertTrue(result);
        assertEquals(-1500.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromSavingsAccountToDebitAccount() {
        BankAccount from = new SavingsAccount("1", "A", 5000.0, 1000.0);
        BankAccount to = new DebitAccount("2", "B", 1000.0);
        TransferService service = new TransferService(new NoCommission(), new DummyNotificationService());

        boolean result = service.transfer(from, to, 3000.0);

        assertTrue(result);
        assertEquals(2000.0, from.getBalance());
        assertEquals(4000.0, to.getBalance());
    }
}
