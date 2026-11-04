package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceFilterTest {
    private final Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
    private final BankAccount richDebit = new DebitAccount("3333333333", "Ivan", 100_000);
    private final BankAccount credit = new CreditAccount("1111111111", "Ivan", 150_000, 1000);
    private final BankAccount debit = new DebitAccount("4444444444", "Olga", 99_999);
    private final BankAccount savings = new SavingsAccount("2222222222", "Anna", 50_000, 1000);
    private BankService bank;

    @BeforeEach
    void setUp() {
        bank = new BankService(accounts, new TransferService(
            amount -> 0.0, message -> {}, new BlockedAccountRegistry(), new TransactionJournal()
        ));
        bank.openAccount(richDebit);
        bank.openAccount(credit);
        bank.openAccount(debit);
        bank.openAccount(savings);
    }

    @Test
    void richAccountsIncludeBoundaryAndKeepRepositoryOrder() {
        List<BankAccount> expected = new ArrayList<>(accounts.findAll());
        expected.removeAll(List.of(debit, savings));

        assertEquals(expected, bank.findRichAccounts(100_000));
        assertEquals(List.of(credit), bank.findRichAccounts(100_001));
        assertTrue(bank.findRichAccounts(200_000).isEmpty());
    }

    @Test
    void debitAccountsExcludeOtherTypesAndKeepRepositoryOrder() {
        List<BankAccount> expected = new ArrayList<>(accounts.findAll());
        expected.removeAll(List.of(credit, savings));

        assertEquals(expected, bank.findDebitAccounts());
    }

    @Test
    void ownerFilterUsesStringEqualityAndKeepsRepositoryOrder() {
        List<BankAccount> expected = new ArrayList<>(accounts.findAll());
        expected.removeAll(List.of(debit, savings));

        assertEquals(expected, bank.findAccountsByOwner(new String("Ivan")));
        assertTrue(bank.findAccountsByOwner("ivan").isEmpty());
        assertTrue(bank.findAccountsByOwner("Unknown").isEmpty());
    }

    @Test
    void emptyRepositoryReturnsEmptyLists() {
        BankService emptyBank = new BankService(new InMemoryRepository<>(), new TransferService(
            amount -> 0.0, message -> {}, new BlockedAccountRegistry(), new TransactionJournal()
        ));

        assertTrue(emptyBank.findRichAccounts(0).isEmpty());
        assertTrue(emptyBank.findDebitAccounts().isEmpty());
        assertTrue(emptyBank.findAccountsByOwner("Ivan").isEmpty());
    }

    @Test
    void filtersDoNotChangeRepositoryAndResultsDoNotExposeItsList() {
        List<BankAccount> before = accounts.findAll();

        List<BankAccount> rich = bank.findRichAccounts(100_000);
        List<BankAccount> debitAccounts = bank.findDebitAccounts();
        List<BankAccount> owned = bank.findAccountsByOwner("Ivan");

        assertThrows(UnsupportedOperationException.class, () -> rich.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> debitAccounts.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> owned.remove(0));
        assertEquals(before, accounts.findAll());
        assertEquals(4, accounts.size());
        assertSame(richDebit, bank.findAccount(richDebit.getId()));
        assertEquals(100_000, richDebit.getBalance());

        bank.closeAccount(richDebit.getId());
        assertEquals(2, rich.size());
        assertTrue(rich.contains(richDebit));
    }
}
