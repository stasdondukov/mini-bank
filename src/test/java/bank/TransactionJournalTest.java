package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionJournalTest {
    private final Transaction tx1 = new Transaction(
        1L, TransactionType.TRANSFER, new AccountNumber("0000000001"), 100.0, TransactionStatus.SUCCESS
    );
    private final Transaction tx2 = new Transaction(
        2L, TransactionType.TRANSFER, new AccountNumber("0000000002"), 200.0, TransactionStatus.SUCCESS
    );
    private final Transaction rejected1 = new Transaction(
        3L, TransactionType.TRANSFER, new AccountNumber("0000000001"), 300.0, TransactionStatus.REJECTED
    );
    private final Transaction rejected2 = new Transaction(
        4L, TransactionType.TRANSFER, new AccountNumber("0000000002"), 400.0, TransactionStatus.REJECTED
    );

    @Test
    void transactionsKeepInsertionOrder() {
        TransactionJournal journal = new TransactionJournal();

        journal.add(tx2);
        journal.add(tx1);

        assertEquals(List.of(tx2, tx1), journal.findAll());
    }

    @Test
    void sameTransactionCanBeAddedTwice() {
        TransactionJournal journal = new TransactionJournal();

        journal.add(tx1);
        journal.add(tx1);

        assertEquals(2, journal.size());
        assertEquals(List.of(tx1, tx1), journal.findAll());
    }

    @Test
    void sizeMatchesNumberOfEntries() {
        TransactionJournal journal = new TransactionJournal();
        assertEquals(0, journal.size());

        journal.add(tx1);
        assertEquals(1, journal.size());

        journal.add(tx2);
        assertEquals(2, journal.size());
    }

    @Test
    void returnedListDoesNotExposeInternalState() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx1);

        List<Transaction> copy = journal.findAll();
        copy.clear();

        assertEquals(1, journal.size());
        assertSame(tx1, journal.findAll().get(0));
    }

    @Test
    void removeRejectedKeepsSuccessOrderAndDuplicates() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(rejected1);
        journal.add(tx2);
        journal.add(rejected1);
        journal.add(rejected2);
        journal.add(tx1);
        journal.add(tx1);
        journal.add(rejected2);

        journal.removeRejected();

        assertEquals(3, journal.size());
        assertEquals(List.of(tx2, tx1, tx1), journal.findAll());
    }

    @Test
    void removeRejectedRemovesAllRejectedTransactions() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(rejected1);
        journal.add(rejected1);
        journal.add(rejected2);

        journal.removeRejected();

        assertEquals(0, journal.size());
        assertTrue(journal.findAll().isEmpty());
    }

    @Test
    void removeRejectedKeepsSuccessfulTransactions() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx2);
        journal.add(tx1);

        journal.removeRejected();

        assertEquals(2, journal.size());
        assertEquals(List.of(tx2, tx1), journal.findAll());
    }

    @Test
    void removeRejectedWorksForEmptyJournal() {
        TransactionJournal journal = new TransactionJournal();

        journal.removeRejected();

        assertEquals(0, journal.size());
        assertTrue(journal.findAll().isEmpty());
    }

    @Test
    void findByAccountKeepsMatchingOrderAndDuplicates() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx2);
        journal.add(rejected1);
        journal.add(tx1);
        journal.add(rejected2);
        journal.add(tx1);

        List<Transaction> result = journal.findByAccount(new AccountNumber("0000000001"));

        assertEquals(List.of(rejected1, tx1, tx1), result);
    }

    @Test
    void findByAccountReturnsEmptyListWhenNothingMatches() {
        TransactionJournal journal = new TransactionJournal();
        AccountNumber number = new AccountNumber("0000000003");
        assertTrue(journal.findByAccount(number).isEmpty());

        journal.add(tx1);
        journal.add(tx2);

        assertTrue(journal.findByAccount(number).isEmpty());
    }

    @Test
    void findByAccountReturnsIndependentList() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx1);
        journal.add(tx2);
        AccountNumber number = new AccountNumber("0000000001");

        List<Transaction> result = journal.findByAccount(number);
        result.clear();
        result.add(rejected2);

        assertEquals(List.of(tx1, tx2), journal.findAll());
        assertEquals(List.of(tx1), journal.findByAccount(number));
    }

    @Test
    void findByAccountReflectsRejectedRemoval() {
        TransactionJournal journal = new TransactionJournal();
        journal.add(tx1);
        journal.add(rejected1);
        journal.add(tx2);

        journal.removeRejected();

        assertEquals(List.of(tx1), journal.findByAccount(new AccountNumber("0000000001")));
    }
}
