package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class AccountStreamDemo {

    public static void main(String[] args) {
        List<BankAccount> accounts = List.of(
            new DebitAccount("1111111111", "Ivan", 99_999),
            new DebitAccount("2222222222", "Olga", 100_000),
            new CreditAccount("3333333333", "Petr", 150_000, 1000),
            new SavingsAccount("4444444444", "Anna", 50_000, 1000)
        );

        List<BankAccount> loopResult = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                loopResult.add(account);
            }
        }

        List<BankAccount> streamResult = accounts.stream()
            .filter(a -> a.getBalance() >= 100_000)
            .toList();

        System.out.println("Loop: " + loopResult);
        System.out.println("Stream: " + streamResult);
        System.out.println("Equal results: " + loopResult.equals(streamResult));

        AtomicInteger predicateCalls = new AtomicInteger();
        Predicate<BankAccount> rich = account -> {
            predicateCalls.incrementAndGet();
            return account.getBalance() >= 100_000;
        };
        boolean found = accounts.stream().anyMatch(rich);

        System.out.println("anyMatch result: " + found);
        System.out.println("Predicate calls: " + predicateCalls.get() + " of " + accounts.size());

        TransferService transfers = new TransferService(
            amount -> 0.0, System.out::println, new BlockedAccountRegistry(), new TransactionJournal()
        );
        BankService bank = new BankService(new InMemoryRepository<>(), transfers);
        for (BankAccount account : accounts) {
            bank.openAccount(account);
        }

        BankAccount account = bank.findFirstRichAccount(100_000)
            .orElseThrow(() -> new IllegalStateException("No rich account"));
        String label = bank.findFirstRichAccount(100_000)
            .map(a -> a.getId().value())
            .orElse("not found");

        System.out.println("First rich account: " + account);
        System.out.println("Account label: " + label);
    }
}
