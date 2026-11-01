package bank;

import java.util.List;
import java.util.function.Predicate;

public class PredicateCompositionDemo {

    public static void main(String[] args) {
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService transfers = new TransferService(
            amount -> 0.0, System.out::println, blockedAccounts, new TransactionJournal()
        );
        BankService bank = new BankService(new InMemoryRepository<>(), transfers);
        bank.openAccount(new DebitAccount("1111111111", "Ivan", 100_000));
        bank.openAccount(new DebitAccount("2222222222", "Olga", 200_000));
        bank.openAccount(new DebitAccount("3333333333", "Petr", 99_999));
        bank.openAccount(new CreditAccount("4444444444", "Anna", 500, 1000));
        bank.openAccount(new CreditAccount("5555555555", "Maria", 150_000, 1000));
        bank.openAccount(new SavingsAccount("6666666666", "Oleg", 200_000, 1000));
        blockedAccounts.block(new AccountNumber("2222222222"));

        double minimumBalance = 100_000;
        Predicate<BankAccount> rich = a -> a.getBalance() >= minimumBalance;
        Predicate<BankAccount> notBlocked = a -> !blockedAccounts.isBlocked(a.getId());
        Predicate<BankAccount> debit = DebitAccount.class::isInstance;
        Predicate<BankAccount> credit = CreditAccount.class::isInstance;
        Predicate<BankAccount> candidates = rich.and(notBlocked).and(debit);

        System.out.println("Balance at least " + minimumBalance + ": " + bank.findAccounts(rich));
        System.out.println("Not blocked: " + bank.findAccounts(notBlocked));
        System.out.println("Debit accounts: " + bank.findAccounts(debit));
        System.out.println("Rich and not blocked: " + bank.findAccounts(rich.and(notBlocked)));
        System.out.println("Not debit accounts: " + bank.findAccounts(debit.negate()));
        System.out.println("Rich or credit accounts: " + bank.findAccounts(rich.or(credit)));
        List<BankAccount> result = bank.findAccounts(candidates);
        System.out.println("Rich, not blocked debit accounts: " + result);
    }
}
