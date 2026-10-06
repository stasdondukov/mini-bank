package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    @Test
    void successfulTransferChangesBothBalances() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000.0);

        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeAnyBalance() {
        BankAccount from = new DebitAccount("0000000001", "A", 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 3000.0));

        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferNegativeAmount() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(InvalidAmountException.class, () -> service.transfer(from, to, -500.0));

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferZeroAmount() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(InvalidAmountException.class, () -> service.transfer(from, to, 0.0));

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferToSameAccount() {
        BankAccount account = new DebitAccount("0000000001", "A", 10000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(IllegalArgumentException.class, () -> service.transfer(account, account, 3000.0));

        assertEquals(10000.0, account.getBalance());
    }
    
    @Test
    void transferExceedingLimitFails() {
        BankAccount from = new DebitAccount("0000000001", "A", 100000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(TransferLimitExceededException.class, () -> service.transfer(from, to, 50001.0));

        assertEquals(100000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void commissionIsDeductedFromSender() {
        BankAccount from = new DebitAccount("0000000001", "A", 11000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(1.0), notificationService);

        service.transfer(from, to, 10000.0);

        assertEquals(900.0, from.getBalance());
    }

    @Test
    void receiverGetsExactlyTransferAmount() {
        BankAccount from = new DebitAccount("0000000001", "A", 11000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(1.0), notificationService);

        service.transfer(from, to, 10000.0);

        assertEquals(12000.0, to.getBalance());
    }

    @Test
    void transferFailsWhenInsufficientFundsForAmountWithCommission() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(1.0), notificationService);

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 10000.0));

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void transferFromDebitAccountToDebitAccount() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 2000.0);

        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromDebitAccountToSavingsAccount() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new SavingsAccount("0000000002", "B", 2000.0, 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 2000.0);

        assertEquals(3000.0, from.getBalance());
        assertEquals(4000.0, to.getBalance());
    }

    @Test
    void transferFromCreditAccountToDebitAccount() {
        BankAccount from = new CreditAccount("0000000001", "A", 500.0, 3000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 2000.0);

        assertEquals(-1500.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromSavingsAccountToDebitAccount() {
        BankAccount from = new SavingsAccount("0000000001", "A", 5000.0, 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000.0);

        assertEquals(2000.0, from.getBalance());
        assertEquals(4000.0, to.getBalance());
    }

    @Test
    void exactlyOneNotificationSentAfterSuccessfulTransfer() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000.0);

        assertEquals(1, notificationService.getNotificationCount());
        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }

    @Test
    void notificationNotSentAfterFailedTransfer() {
        BankAccount from = new DebitAccount("0000000001", "A", 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 3000.0));

        assertEquals(0, notificationService.getNotificationCount());
        assertNull(notificationService.getLastMessage());
    }

    @Test
    void notificationMessageMatchesExpectedFormat() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(2.0), notificationService);

        service.transfer(from, to, 5000.0);

        assertEquals("Transfer 5000.0 completed", notificationService.getLastMessage());
    }
}
