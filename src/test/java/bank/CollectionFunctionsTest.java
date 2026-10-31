package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionFunctionsTest {
    private final BankAccount empty = new DebitAccount("1111111111", "Ivan", 0);
    private final BankAccount positive = new DebitAccount("2222222222", "Olga", 500);
    private final BankAccount rich = new CreditAccount("3333333333", "Petr", 100_000, 1000);
    private final List<BankAccount> accounts = List.of(empty, positive, rich);

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
