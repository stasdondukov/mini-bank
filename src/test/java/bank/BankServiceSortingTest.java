package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceSortingTest {
    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final BankAccount debitHigh = new DebitAccount("6666666666", "Ivan", 6000);
    private final BankAccount debitLow = new DebitAccount("1111111111", "Olga", 1000);
    private final BankAccount creditHigh = new CreditAccount("5555555555", "Petr", 4000, 1000);
    private final BankAccount creditLow = new CreditAccount("2222222222", "Anna", 2000, 1000);
    private final BankAccount savingsHigh = new SavingsAccount("4444444444", "Oleg", 5000, 1000);
    private final BankAccount savingsLow = new SavingsAccount("3333333333", "Elena", 3000, 1000);
    private final Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
    private BankService bank;

    @BeforeEach
    void setUp() {
        TransferService transfers = new TransferService(
            amount -> 0.0, message -> {}, new BlockedAccountRegistry(), new TransactionJournal()
        );
        bank = new BankService(accounts, transfers);
        bank.openAccount(debitHigh);
        bank.openAccount(creditHigh);
        bank.openAccount(savingsHigh);
        bank.openAccount(creditLow);
        bank.openAccount(debitLow);
        bank.openAccount(savingsLow);
    }

    @Test
    void sortsByBalanceAscending() {
        assertEquals(
            List.of(debitLow, creditLow, savingsLow, creditHigh, savingsHigh, debitHigh),
            bank.sortedAccounts(byBalance)
        );
    }

    @Test
    void sortsByBalanceDescending() {
        Comparator<BankAccount> byBalanceDesc = byBalance.reversed();

        assertEquals(
            List.of(debitHigh, savingsHigh, creditHigh, savingsLow, creditLow, debitLow),
            bank.sortedAccounts(byBalanceDesc)
        );
    }

    @Test
    void sortsByAccountNumber() {
        Comparator<BankAccount> byNumber = Comparator.comparing(a -> a.getId().value());

        assertEquals(
            List.of(debitLow, creditLow, savingsLow, savingsHigh, creditHigh, debitHigh),
            bank.sortedAccounts(byNumber)
        );
    }

    @Test
    void sortsByTypeThenBalance() {
        Comparator<BankAccount> byTypeThenBalance = Comparator
            .comparing((BankAccount a) -> a.getClass().getSimpleName())
            .thenComparingDouble(BankAccount::getBalance);

        assertEquals(
            List.of(creditLow, creditHigh, debitLow, debitHigh, savingsLow, savingsHigh),
            bank.sortedAccounts(byTypeThenBalance)
        );
    }

    @Test
    void sortingAndChangingResultDoNotChangeRepository() {
        List<BankAccount> before = accounts.findAll();
        Comparator<Object> byText = Comparator.comparing(Object::toString);

        List<BankAccount> result = bank.sortedAccounts(byText);

        assertEquals(before, accounts.findAll());
        result.clear();
        result.add(new DebitAccount("7777777777", "Maria", 100));
        assertEquals(before, accounts.findAll());
        assertEquals(6, accounts.size());
        assertSame(debitHigh, bank.findAccount(debitHigh.getId()));
        assertEquals(6000, debitHigh.getBalance());
        assertNull(bank.findAccount(new AccountNumber("7777777777")));
    }

    @Test
    void emptyBankReturnsEmptyList() {
        BankService emptyBank = new BankService(new InMemoryRepository<>(), new TransferService(
            amount -> 0.0, message -> {}, new BlockedAccountRegistry(), new TransactionJournal()
        ));

        assertTrue(emptyBank.sortedAccounts(byBalance).isEmpty());
    }
}
