package bank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionJournal {
    private final List<Transaction> transactions = new ArrayList<>();
    private final TransactionIndex index = new TransactionIndex();

    public void add(Transaction transaction) {
        index.add(transaction);
        transactions.add(transaction);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> findByAccount(AccountNumber number) {
        return index.findByAccount(number);
    }

    public void removeRejected() {
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.status() == TransactionStatus.REJECTED) {
                index.remove(tx);
                iterator.remove();
            }
        }
    }

    public int size() {
        return transactions.size();
    }
}
