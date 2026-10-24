package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {
    private Repository<AccountNumber, BankAccount> accounts;
    private BankService bankService;
    private BlockedAccountRegistry blockedAccounts;
    private FakeNotificationService notificationService;
    private TransactionJournal journal;
    private BankAccount from;
    private BankAccount to;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryRepository<>();
        blockedAccounts = new BlockedAccountRegistry();
        notificationService = new FakeNotificationService();
        journal = new TransactionJournal();
        TransferService transferService = new TransferService(
            new NoCommission(), notificationService, blockedAccounts, journal
        );
        bankService = new BankService(accounts, transferService);
        from = new DebitAccount("1111111111", "Ivan", 10000.0);
        to = new DebitAccount("2222222222", "Petr", 2000.0);
    }

    @Test
    void openedAccountCanBeFound() {
        AccountNumber number = new AccountNumber("0000000001");
        BankAccount account = new DebitAccount(number, "Ivan", 1000.0);

        bankService.openAccount(account);

        assertSame(account, bankService.findAccount(new AccountNumber("0000000001")));
        assertSame(account, accounts.findById(number));
    }

    @Test
    void openingSameNumberReplacesAccount() {
        AccountNumber number = new AccountNumber("0000000001");
        BankAccount oldAccount = new DebitAccount(number, "Ivan", 1000.0);
        BankAccount newAccount = new DebitAccount(new AccountNumber("0000000001"), "Petr", 5000.0);
        bankService.openAccount(oldAccount);

        bankService.openAccount(newAccount);

        assertEquals(1, accounts.size());
        assertSame(newAccount, bankService.findAccount(number));
    }

    @Test
    void closedAccountCannotBeFound() {
        BankAccount first = new DebitAccount("0000000001", "Ivan", 1000.0);
        BankAccount second = new DebitAccount("0000000002", "Petr", 2000.0);
        bankService.openAccount(first);
        bankService.openAccount(second);

        assertTrue(bankService.closeAccount(new AccountNumber("0000000001")));

        assertNull(bankService.findAccount(first.getNumber()));
        assertFalse(accounts.existsById(first.getNumber()));
        assertSame(second, bankService.findAccount(second.getNumber()));
        assertEquals(1, accounts.size());
    }

    @Test
    void openingNullAccountIsForbidden() {
        assertThrows(IllegalArgumentException.class, () -> bankService.openAccount(null));
        assertEquals(0, accounts.size());
    }

    @Test
    void missingAccountReturnsNull() {
        assertNull(bankService.findAccount(new AccountNumber("0000000001")));
    }

    @Test
    void closingMissingAccountReturnsFalse() {
        BankAccount account = new DebitAccount("0000000001", "Ivan", 1000.0);
        bankService.openAccount(account);

        assertFalse(bankService.closeAccount(new AccountNumber("0000000002")));

        assertSame(account, bankService.findAccount(account.getNumber()));
        assertEquals(1, accounts.size());
    }

    @Test
    void transferByAccountNumbersChangesBothBalances() {
        bankService.openAccount(from);
        bankService.openAccount(to);

        bankService.transfer(new AccountNumber("1111111111"), new AccountNumber("2222222222"), 3000.0);

        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
        assertEquals(1, notificationService.getNotificationCount());
        assertEquals(1, journal.size());
        Transaction transaction = journal.findAll().get(0);
        assertEquals(from.getNumber(), transaction.account());
        assertEquals(3000.0, transaction.amount());
        assertEquals(TransactionStatus.SUCCESS, transaction.status());
    }

    @Test
    void transferWithMissingSourceDoesNotChangeAnything() {
        bankService.openAccount(to);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> bankService.transfer(from.getNumber(), to.getNumber(), 3000.0)
        );

        assertEquals("Source account not found", ex.getMessage());
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
        assertEquals(0, journal.size());
    }

    @Test
    void transferWithMissingDestinationDoesNotWithdrawMoney() {
        bankService.openAccount(from);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> bankService.transfer(from.getNumber(), to.getNumber(), 3000.0)
        );

        assertEquals("Destination account not found", ex.getMessage());
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
        assertEquals(0, journal.size());
    }

    @Test
    void transferFromBlockedSourceDoesNotChangeAnything() {
        bankService.openAccount(from);
        bankService.openAccount(to);
        blockedAccounts.block(from.getNumber());

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> bankService.transfer(from.getNumber(), to.getNumber(), 3000.0)
        );

        assertEquals("Source account is blocked", ex.getMessage());
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
        assertEquals(0, journal.size());
    }

    @Test
    void transferWithoutEnoughMoneyDoesNotChangeAnything() {
        bankService.openAccount(from);
        bankService.openAccount(to);

        assertThrows(
            InsufficientFundsException.class,
            () -> bankService.transfer(from.getNumber(), to.getNumber(), 11000.0)
        );

        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
        assertEquals(0, journal.size());
    }

    @Test
    void destinationFailureRestoresBothBalances() {
        BankAccount failingDestination = new DebitAccount("2222222222", "Petr", 2000.0) {
            @Override
            public void deposit(double amount) {
                super.deposit(amount);
                throw new IllegalStateException("Deposit failed");
            }
        };
        bankService.openAccount(from);
        bankService.openAccount(failingDestination);

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> bankService.transfer(from.getNumber(), failingDestination.getNumber(), 3000.0)
        );

        assertEquals("Deposit failed", ex.getMessage());
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, failingDestination.getBalance());
        assertEquals(0, notificationService.getNotificationCount());
        assertEquals(0, journal.size());
    }
}
