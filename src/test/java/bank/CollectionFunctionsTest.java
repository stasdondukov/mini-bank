package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionFunctionsTest {
    private final BankAccount empty = new DebitAccount("1111111111", "Ivan", 0);
    private final BankAccount positive = new DebitAccount("2222222222", "Olga", 500);
    private final BankAccount rich = new CreditAccount("3333333333", "Petr", 100_000, 1000);
    private final List<BankAccount> accounts = List.of(empty, positive, rich);

    @Test
    void mapsAccountsToSummaries() {
        Function<BankAccount, AccountSummary> toSummary = account -> new AccountSummary(
            account.getId(), account.getClass().getSimpleName(), account.getBalance()
        );
        BankAccount savings = new SavingsAccount("4444444444", "Anna", 3000, 1000);

        List<AccountSummary> result = CollectionFunctions.map(List.of(positive, rich, savings), toSummary);

        assertEquals(List.of(
            new AccountSummary(positive.getId(), "DebitAccount", 500),
            new AccountSummary(rich.getId(), "CreditAccount", 100_000),
            new AccountSummary(savings.getId(), "SavingsAccount", 3000)
        ), result);
    }

    @Test
    void mapsAccountsToLabels() {
        Function<BankAccount, String> label = account -> account.getId() + " : " + account.getBalance();

        assertEquals(List.of(
            "AccountNumber[value=1111111111] : 0.0",
            "AccountNumber[value=2222222222] : 500.0",
            "AccountNumber[value=3333333333] : 100000.0"
        ), CollectionFunctions.map(accounts, label));
    }

    @Test
    void mappingEmptyListDoesNotCallMapper() {
        Function<BankAccount, String> mapper = account -> {
            throw new AssertionError("Mapper must not be called for an empty list");
        };

        assertTrue(CollectionFunctions.map(List.<BankAccount>of(), mapper).isEmpty());
    }

    @Test
    void mappingPreservesOrderAndDuplicatesAndReturnsIndependentList() {
        List<BankAccount> source = new ArrayList<>(List.of(rich, positive, rich, empty));

        List<Double> result = CollectionFunctions.map(source, BankAccount::getBalance);

        assertEquals(List.of(100_000.0, 500.0, 100_000.0, 0.0), result);
        assertEquals(List.of(rich, positive, rich, empty), source);
        result.clear();
        assertEquals(List.of(rich, positive, rich, empty), source);
    }

    @Test
    void mappingAcceptsSupertypeInputAndSubtypeOutput() {
        List<DebitAccount> source = List.of(new DebitAccount("4444444444", "Anna", 1000));
        Function<BankAccount, String> mapper = BankAccount::getOwner;

        List<CharSequence> result = CollectionFunctions.map(source, mapper);

        assertEquals(List.of("Anna"), result);
    }

    @Test
    void summaryKeepsBalanceAtTimeOfMapping() {
        List<AccountSummary> result = CollectionFunctions.map(List.of(positive), account -> new AccountSummary(
            account.getId(), account.getClass().getSimpleName(), account.getBalance()
        ));

        positive.deposit(100);

        assertEquals(500, result.get(0).balance());
        assertEquals(600, positive.getBalance());
    }

    @Test
    void filtersPositiveBalances() {
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        assertEquals(List.of(positive, rich), CollectionFunctions.filter(accounts, positiveBalance));
    }

    @Test
    void filtersRichAccountsIncludingBoundary() {
        Predicate<BankAccount> condition = account -> account.getBalance() >= 100_000;

        assertEquals(List.of(rich), CollectionFunctions.filter(accounts, condition));
    }

    @Test
    void filtersDebitAccountsUsingMethodReference() {
        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;

        assertEquals(List.of(empty, positive), CollectionFunctions.filter(accounts, debitOnly));
    }

    @Test
    void emptySourceAndNoMatchesReturnEmptyLists() {
        assertTrue(CollectionFunctions.filter(List.<BankAccount>of(), account -> true).isEmpty());
        assertTrue(CollectionFunctions.filter(accounts, account -> false).isEmpty());
    }

    @Test
    void preservesOrderAndDuplicatesWithoutChangingSource() {
        List<BankAccount> source = new ArrayList<>(List.of(positive, empty, rich, positive));

        List<BankAccount> result = CollectionFunctions.filter(source, account -> account.getBalance() > 0);

        assertEquals(List.of(positive, rich, positive), result);
        assertEquals(List.of(positive, empty, rich, positive), source);
        result.clear();
        assertEquals(List.of(positive, empty, rich, positive), source);
    }

    @Test
    void acceptsPredicateForSupertype() {
        List<DebitAccount> source = List.of(new DebitAccount("4444444444", "Anna", 1000));
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        assertEquals(source, CollectionFunctions.filter(source, positiveBalance));
    }
}
