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
}
