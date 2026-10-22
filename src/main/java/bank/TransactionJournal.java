package bank;

import java.util.ArrayList;
import java.util.List;

public class TransactionJournal {
    private final List<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public int size() {
        return transactions.size();
    }
}
