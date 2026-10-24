package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionIndexTest {
    private final Transaction tx1 = new Transaction(
        1L, TransactionType.TRANSFER, new AccountNumber("1111111111"), 100.0, TransactionStatus.SUCCESS
    );
    private final Transaction tx2 = new Transaction(
        2L, TransactionType.TRANSFER, new AccountNumber("2222222222"), 200.0, TransactionStatus.SUCCESS
    );
    private final Transaction tx3 = new Transaction(
        3L, TransactionType.TRANSFER, new AccountNumber("1111111111"), 300.0, TransactionStatus.REJECTED
    );

    @Test
    void transactionCanBeFoundByEqualAccountNumber() {
        TransactionIndex index = new TransactionIndex();
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");
        Transaction transaction = new Transaction(
            1L, TransactionType.TRANSFER, stored, 100.0, TransactionStatus.SUCCESS
        );

        index.add(transaction);

        assertNotSame(stored, lookup);
        assertEquals(1, index.findByAccount(lookup).size());
        assertSame(transaction, index.findByAccount(lookup).get(0));
    }

    @Test
    void transactionsAreGroupedWithOrderAndDuplicatesPreserved() {
        TransactionIndex index = new TransactionIndex();
        index.add(tx1);
        index.add(tx2);
        index.add(tx3);
        index.add(tx1);

        assertEquals(List.of(tx1, tx3, tx1), index.findByAccount(tx1.account()));
        assertEquals(List.of(tx2), index.findByAccount(tx2.account()));
    }

    @Test
    void unknownAccountReturnsIndependentEmptyList() {
        TransactionIndex index = new TransactionIndex();
        index.add(tx1);
        AccountNumber unknown = new AccountNumber("3333333333");

        List<Transaction> result = index.findByAccount(unknown);
        assertTrue(result.isEmpty());
        result.add(tx2);

        assertTrue(index.findByAccount(unknown).isEmpty());
        assertEquals(List.of(tx1), index.findByAccount(tx1.account()));
    }

    @Test
    void returnedListDoesNotExposeIndexState() {
        TransactionIndex index = new TransactionIndex();
        index.add(tx1);

        List<Transaction> result = index.findByAccount(tx1.account());
        result.clear();
        result.add(tx2);

        assertEquals(List.of(tx1), index.findByAccount(tx1.account()));
    }

    @Test
    void nullTransactionIsRejectedWithoutChangingIndex() {
        TransactionIndex index = new TransactionIndex();
        index.add(tx1);

        assertThrows(IllegalArgumentException.class, () -> index.add(null));

        assertEquals(List.of(tx1), index.findByAccount(tx1.account()));
    }

    @Test
    void removalAffectsOneOccurrenceAndKeepsOtherAccounts() {
        TransactionIndex index = new TransactionIndex();
        index.add(tx1);
        index.add(tx1);
        index.add(tx2);

        index.remove(tx1);
        assertEquals(List.of(tx1), index.findByAccount(tx1.account()));

        index.remove(tx1);
        assertTrue(index.findByAccount(tx1.account()).isEmpty());

        index.remove(tx1);
        assertEquals(List.of(tx2), index.findByAccount(tx2.account()));
    }
}
