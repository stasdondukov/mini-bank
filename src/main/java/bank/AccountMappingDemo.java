package bank;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AccountMappingDemo {

    public static void main(String[] args) {
        List<BankAccount> accounts = List.of(
            new DebitAccount("1111111111", "Ivan", 1000),
            new CreditAccount("2222222222", "Olga", 2000, 1000),
            new SavingsAccount("3333333333", "Petr", 3000, 1000)
        );

        Function<BankAccount, AccountSummary> toSummary = account -> new AccountSummary(
            account.getId(),
            account.getClass().getSimpleName(),
            account.getBalance()
        );
        List<AccountSummary> summaries = CollectionFunctions.map(accounts, toSummary);
        System.out.println("Summaries: " + summaries);

        Consumer<AccountSummary> printer = summary -> System.out.println(summary);
        CollectionFunctions.forEach(summaries, printer);
        CollectionFunctions.forEach(summaries, System.out::println);

        Function<BankAccount, String> label =
            account -> account.getId() + " : " + account.getBalance();
        List<String> labels = CollectionFunctions.map(accounts, label);
        System.out.println("Labels: " + labels);
    }
}
