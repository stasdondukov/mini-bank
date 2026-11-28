package bank.application;

import bank.domain.AccountNumber;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionJournal {
    private final List<Transaction> transactions = new ArrayList<>();
    private final TransactionIndex index = new TransactionIndex();

    public synchronized void add(Transaction transaction) {
        index.add(transaction);
        transactions.add(transaction);
    }

    public synchronized List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public synchronized List<Transaction> findByAccount(AccountNumber number) {
        return index.findByAccount(number);
    }

    public synchronized void removeRejected() {
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.status() == TransactionStatus.REJECTED) {
                index.remove(tx);
                iterator.remove();
            }
        }
    }

    public synchronized int size() {
        return transactions.size();
    }
}
