package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class PredicateCompositionTest {
    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final BankAccount richDebit = new DebitAccount("1111111111", "Ivan", 100_000);
    private final BankAccount blockedDebit = new DebitAccount("2222222222", "Olga", 200_000);
    private final BankAccount poorDebit = new DebitAccount("3333333333", "Petr", 99_999);
    private final BankAccount poorCredit = new CreditAccount("4444444444", "Anna", 500, 1000);
    private final BankAccount richCredit = new CreditAccount("5555555555", "Maria", 150_000, 1000);
    private final BankAccount savings = new SavingsAccount("6666666666", "Oleg", 200_000, 1000);
    private final Predicate<BankAccount> rich = a -> a.getBalance() >= 100_000;
    private final Predicate<BankAccount> notBlocked = a -> !blockedAccounts.isBlocked(a.getId());
    private final Predicate<BankAccount> debit = DebitAccount.class::isInstance;
    private final Predicate<BankAccount> credit = CreditAccount.class::isInstance;
    private BankService bank;

    @BeforeEach
    void setUp() {
        TransferService transfers = new TransferService(
            amount -> 0.0, message -> {}, blockedAccounts, new TransactionJournal()
        );
        bank = new BankService(new InMemoryRepository<>(), transfers);
        bank.openAccount(richDebit);
        bank.openAccount(blockedDebit);
        bank.openAccount(poorDebit);
        bank.openAccount(poorCredit);
        bank.openAccount(richCredit);
        bank.openAccount(savings);
        blockedAccounts.block(new AccountNumber("2222222222"));
    }

    @Test
    void balanceThresholdIncludesEqualBalance() {
        double minimumBalance = 100_000;
        Predicate<BankAccount> enoughBalance = a -> a.getBalance() >= minimumBalance;

        List<BankAccount> result = bank.findAccounts(enoughBalance);

        assertEquals(4, result.size());
        assertTrue(result.containsAll(List.of(richDebit, blockedDebit, richCredit, savings)));
    }

    @Test
    void findsUnblockedAccounts() {
        List<BankAccount> result = bank.findAccounts(notBlocked);

        assertEquals(5, result.size());
        assertTrue(result.containsAll(List.of(richDebit, poorDebit, poorCredit, richCredit, savings)));
    }

    @Test
    void findsOnlyDebitAccounts() {
        List<BankAccount> result = bank.findAccounts(debit);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(richDebit, blockedDebit, poorDebit)));
    }

    @Test
    void combinesRichAndNotBlocked() {
        List<BankAccount> result = bank.findAccounts(rich.and(notBlocked));

        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(richDebit, richCredit, savings)));
    }

    @Test
    void negatesDebitCondition() {
        List<BankAccount> result = bank.findAccounts(debit.negate());

        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(poorCredit, richCredit, savings)));
    }

    @Test
    void combinesRichOrCreditWithoutDuplicates() {
        List<BankAccount> result = bank.findAccounts(rich.or(credit));

        assertEquals(5, result.size());
        assertTrue(result.containsAll(List.of(richDebit, blockedDebit, poorCredit, richCredit, savings)));
    }

    @Test
    void combinesThreeConditionsForCandidates() {
        Predicate<BankAccount> candidates = rich.and(notBlocked).and(debit);

        assertEquals(List.of(richDebit), bank.findAccounts(candidates));
    }

    @Test
    void composedConditionUsesCurrentBlockingState() {
        Predicate<BankAccount> candidates = rich.and(notBlocked).and(debit);
        blockedAccounts.block(richDebit.getId());

        assertTrue(bank.findAccounts(candidates).isEmpty());

        blockedAccounts.unblock(richDebit.getId());
        assertEquals(List.of(richDebit), bank.findAccounts(candidates));
    }
}
