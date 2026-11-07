package bank;

import java.util.Comparator;
import java.util.List;

public class AccountUtils {

    public static double totalBalance(List<? extends BankAccount> accounts) {
        return accounts.stream()
            .mapToDouble(BankAccount::getBalance)
            .sum();
    }

    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount("1111111111", "Demo1", 1000.0));
        target.add(new DebitAccount("2222222222", "Demo2", 2000.0));

    }

    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            throw new IllegalArgumentException("List of accounts cannot be null or empty");
        }

        T first = accounts.get(0);
        if (accounts.size() == 1 || Double.isNaN(first.getBalance())) {
            return first;
        }

        return accounts.stream()
            .filter(account -> !Double.isNaN(account.getBalance()))
            .max(Comparator.comparingDouble(account -> {
                double balance = account.getBalance();
                return balance == 0.0 ? 0.0 : balance;
            }))
            .orElseThrow();
    }
}
