package bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankReports {
    public Map<AccountNumber, Integer> countByAccount(List<Transaction> transactions) {
        Map<AccountNumber, Integer> result = new HashMap<>();
        for (Transaction tx : transactions) {
            AccountNumber number = tx.account();
            int old = result.getOrDefault(number, 0);
            result.put(number, old + 1);
        }
        return result;
    }

    public Map<AccountNumber, Double> totalAmountByAccount(List<Transaction> transactions) {
        Map<AccountNumber, Double> result = new HashMap<>();
        for (Transaction tx : transactions) {
            AccountNumber number = tx.account();
            double old = result.getOrDefault(number, 0.0);
            result.put(number, old + tx.amount());
        }
        return result;
    }
}
