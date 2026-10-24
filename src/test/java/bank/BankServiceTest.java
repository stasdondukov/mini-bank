package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {
    private Repository<AccountNumber, BankAccount> accounts;
    private BankService bankService;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryRepository<>();
        TransferService transferService = new TransferService(
            new NoCommission(), new FakeNotificationService(),
            new BlockedAccountRegistry(), new TransactionJournal()
        );
        bankService = new BankService(accounts, transferService);
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
}
