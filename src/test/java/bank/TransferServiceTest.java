package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    @Test
    void successfulTransferIsRecordedInJournal() {
        BankAccount from = new DebitAccount("0000000001", "A", 11000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new PercentCommission(1.0), notificationService, new BlockedAccountRegistry(), journal
        );

        service.transfer(from, to, 10000.0);

        assertEquals(1, journal.size());
        Transaction transaction = journal.findAll().get(0);
        assertEquals(1L, transaction.id());
        assertEquals(TransactionType.TRANSFER, transaction.type());
        assertEquals(from.getNumber(), transaction.account());
        assertEquals(10000.0, transaction.amount());
        assertEquals(TransactionStatus.SUCCESS, transaction.status());
    }

    @Test
    void successfulTransfersAreRecordedInOrder() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new NoCommission(), new FakeNotificationService(), new BlockedAccountRegistry(), journal
        );

        service.transfer(from, to, 500.0);
        service.transfer(from, to, 300.0);

        List<Transaction> transactions = journal.findAll();
        assertEquals(2, transactions.size());
        assertEquals(1L, transactions.get(0).id());
        assertEquals(500.0, transactions.get(0).amount());
        assertEquals(2L, transactions.get(1).id());
        assertEquals(300.0, transactions.get(1).amount());
    }

    @Test
    void completedTransferIsRecordedWhenNotificationFails() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        TransactionJournal journal = new TransactionJournal();
        NotificationService notificationService = message -> {
            throw new IllegalStateException("Notification failed");
        };
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), journal
        );

        assertThrows(IllegalStateException.class, () -> service.transfer(from, to, 2000.0));

        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
        assertEquals(1, journal.size());
        assertEquals(TransactionStatus.SUCCESS, journal.findAll().get(0).status());
    }

    @Test
    void transferFromBlockedAccountDoesNotChangeBalancesOrSendNotification() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, journal
        );
        blockedAccounts.block(new AccountNumber("0000000001"));

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> service.transfer(from, to, 2000.0)
        );

        assertEquals("Source account is blocked", ex.getMessage());
        assertEquals(5000.0, from.getBalance());
        assertEquals(1000.0, to.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
        assertNull(notificationService.getLastMessage());
        assertEquals(0, journal.size());
    }

    @Test
    void transferWorksAfterSourceAccountIsUnblocked() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, new TransactionJournal()
        );
        blockedAccounts.block(from.getNumber());
        blockedAccounts.unblock(from.getNumber());

        service.transfer(from, to, 2000.0);

        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void blockedDestinationCanReceiveTransfer() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, new TransactionJournal()
        );
        blockedAccounts.block(to.getNumber());

        service.transfer(from, to, 2000.0);

        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void successfulTransferChangesBothBalances() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 3000.0);

        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        BankAccount from = new DebitAccount("0000000001", "A", 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), journal
        );

        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 3000.0));
        assertEquals("Insufficient funds", ex.getMessage());
        assertEquals(0, journal.size());

        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }


 
 
 
 
@Test
    void negativeTransferThrowsException() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        InvalidAmountException ex = assertThrows(InvalidAmountException.class, () -> service.transfer(from, to, -500.0));
        assertEquals("Transfer amount must be positive", ex.getMessage());

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void zeroTransferThrowsException() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        InvalidAmountException ex = assertThrows(InvalidAmountException.class, () -> service.transfer(from, to, 0.0));
        assertEquals("Transfer amount must be positive", ex.getMessage());

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void transferToSameAccountThrowsException() {
        BankAccount account = new DebitAccount("0000000001", "A", 10000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.transfer(account, account, 3000.0));
        assertEquals("Cannot transfer to the same account", ex.getMessage());

        assertEquals(10000.0, account.getBalance());
    }
    
    @Test
    void transferOverLimitThrowsException() {
        BankAccount from = new DebitAccount("0000000001", "A", 100000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        TransferLimitExceededException ex = assertThrows(TransferLimitExceededException.class, () -> service.transfer(from, to, 50001.0));
        assertEquals("Transfer limit exceeded", ex.getMessage());

        assertEquals(100000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void commissionIsDeductedFromSender() {
        BankAccount from = new DebitAccount("0000000001", "A", 11000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new PercentCommission(1.0), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 10000.0);

        assertEquals(900.0, from.getBalance());
    }

    @Test
    void receiverGetsExactlyTransferAmount() {
        BankAccount from = new DebitAccount("0000000001", "A", 11000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new PercentCommission(1.0), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 10000.0);

        assertEquals(12000.0, to.getBalance());
    }

    @Test
    void transferFailsWhenInsufficientFundsForAmountWithCommission() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new PercentCommission(1.0), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 10000.0));

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void transferFromDebitAccountToDebitAccount() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 2000.0);

        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromDebitAccountToSavingsAccount() {
        BankAccount from = new DebitAccount("0000000001", "A", 5000.0);
        BankAccount to = new SavingsAccount("0000000002", "B", 2000.0, 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 2000.0);

        assertEquals(3000.0, from.getBalance());
        assertEquals(4000.0, to.getBalance());
    }

    @Test
    void transferFromCreditAccountToDebitAccount() {
        BankAccount from = new CreditAccount("0000000001", "A", 500.0, 3000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 2000.0);

        assertEquals(-1500.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromSavingsAccountToDebitAccount() {
        BankAccount from = new SavingsAccount("0000000001", "A", 5000.0, 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 1000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 3000.0);

        assertEquals(2000.0, from.getBalance());
        assertEquals(4000.0, to.getBalance());
    }

    @Test
    void exactlyOneNotificationSentAfterSuccessfulTransfer() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 3000.0);

        assertEquals(1, notificationService.getNotificationCount());
        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }

    @Test
    void notificationNotSentAfterFailedTransfer() {
        BankAccount from = new DebitAccount("0000000001", "A", 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        assertThrows(InsufficientFundsException.class, () -> service.transfer(from, to, 3000.0));

        assertEquals(0, notificationService.getNotificationCount());
        assertNull(notificationService.getLastMessage());
    }

    @Test
    void notificationMessageMatchesExpectedFormat() {
        BankAccount from = new DebitAccount("0000000001", "A", 10000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new PercentCommission(2.0), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 5000.0);

        assertEquals("Transfer 5000.0 completed", notificationService.getLastMessage());
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        BankAccount from = new DebitAccount("0000000001", "A", 1000.0);
        BankAccount to = new DebitAccount("0000000002", "B", 2000.0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(
            new NoCommission(), notificationService, new BlockedAccountRegistry(), new TransactionJournal()
        );

        assertThrows(
            InsufficientFundsException.class,
            () -> service.transfer(from, to, 5000.0)
        );

        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }
}
