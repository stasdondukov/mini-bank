package bank;

import java.util.ArrayList;
import java.util.List;

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
    }
}
