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
}
