package bank;

import java.util.List;

public class AccountUtils {

    public static double totalBalance(List<? extends BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }


        return total;
    }

    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount("1111111111", "Demo1", 1000.0));
        target.add(new DebitAccount("2222222222", "Demo2", 2000.0));

    }

    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            throw new IllegalArgumentException("List of accounts cannot be null or empty");
        }
        
        T richestAccount = accounts.get(0);
        for (int i = 1; i < accounts.size(); i++) {
            T current = accounts.get(i);
            if (current.getBalance() > richestAccount.getBalance()) {
                richestAccount = current;
            }
        }
        
        return richestAccount;
    }
}
