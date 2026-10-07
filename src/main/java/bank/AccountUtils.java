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
}
