package bank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AccountSortingDemo {

    public static void main(String[] args) {
        List<BankAccount> accounts = List.of(
            new DebitAccount("1111111111", "Ivan", 3000),
            new DebitAccount("2222222222", "Olga", 1000),
            new DebitAccount("3333333333", "Petr", 0),
            new DebitAccount("4444444444", "Anna", 1000)
        );

        Comparator<BankAccount> anonymousComparator = new Comparator<>() {
            @Override
            public int compare(BankAccount a, BankAccount b) {
                return Double.compare(a.getBalance(), b.getBalance());
            }
        };

        List<BankAccount> first = new ArrayList<>(accounts);
        first.sort(anonymousComparator);
        System.out.println("Anonymous class: " + first);

        Comparator<BankAccount> lambdaComparator =
            (a, b) -> Double.compare(a.getBalance(), b.getBalance());

        List<BankAccount> second = new ArrayList<>(accounts);
        second.sort(lambdaComparator);
        System.out.println("Lambda: " + second);

        Comparator<BankAccount> byBalance =
            Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> third = new ArrayList<>(accounts);
        third.sort(byBalance);
        System.out.println("Comparator.comparingDouble: " + third);

        TransferService transfers = new TransferService(
            amount -> 0.0, System.out::println,
            new BlockedAccountRegistry(), new TransactionJournal()
        );
        BankService bank = new BankService(new InMemoryRepository<>(), transfers);
        for (BankAccount account : accounts) {
            bank.openAccount(account);
        }
        bank.openAccount(new CreditAccount("5555555555", "Maria", 2000, 1000));
        bank.openAccount(new CreditAccount("6666666666", "Oleg", 500, 1000));
        bank.openAccount(new SavingsAccount("7777777777", "Elena", 4000, 1000));

        Comparator<BankAccount> byBalanceDesc = byBalance.reversed();
        Comparator<BankAccount> byNumber = Comparator.comparing(a -> a.getId().value());
        Comparator<BankAccount> byTypeThenBalance = Comparator
            .comparing((BankAccount a) -> a.getClass().getSimpleName())
            .thenComparingDouble(BankAccount::getBalance);

        System.out.println("Balance ascending: " + bank.sortedAccounts(byBalance));
        System.out.println("Balance descending: " + bank.sortedAccounts(byBalanceDesc));
        System.out.println("Account number: " + bank.sortedAccounts(byNumber));
        System.out.println("Account type, then balance: " + bank.sortedAccounts(byTypeThenBalance));
    }
}
