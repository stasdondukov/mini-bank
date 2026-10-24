package bank;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BankReportsTest {
    private final BankReports reports = new BankReports();
    private final AccountNumber first = new AccountNumber("1111111111");
    private final AccountNumber second = new AccountNumber("2222222222");

    @Test
    void emptyJournalProducesEmptyReports() {
        TransactionJournal journal = new TransactionJournal();

        assertTrue(reports.countByAccount(journal.findAll()).isEmpty());
        assertTrue(reports.totalAmountByAccount(journal.findAll()).isEmpty());
    }

    @Test
    void oneTransactionProducesOneAccountEntry() {
        List<Transaction> transactions = List.of(
            new Transaction(1L, TransactionType.TRANSFER, first, 125.5, TransactionStatus.SUCCESS)
        );

        assertEquals(Map.of(first, 1), reports.countByAccount(transactions));
        assertEquals(Map.of(first, 125.5), reports.totalAmountByAccount(transactions));
    }

    @Test
    void differentAccountsAreCountedSeparately() {
        List<Transaction> transactions = List.of(
            new Transaction(1L, TransactionType.TRANSFER, first, 100.25, TransactionStatus.SUCCESS),
            new Transaction(2L, TransactionType.TRANSFER, second, 200.5, TransactionStatus.SUCCESS),
            new Transaction(3L, TransactionType.TRANSFER, first, 20.75, TransactionStatus.SUCCESS)
        );

        assertEquals(Map.of(first, 2, second, 1), reports.countByAccount(transactions));
        assertEquals(Map.of(first, 121.0, second, 200.5), reports.totalAmountByAccount(transactions));
    }

    @Test
    void repeatedTransactionsForOneAccountAreAllIncluded() {
        Transaction tx1 = new Transaction(
            1L, TransactionType.TRANSFER, first, 10.1, TransactionStatus.SUCCESS
        );
        Transaction tx2 = new Transaction(
            2L, TransactionType.TRANSFER, first, 20.2, TransactionStatus.REJECTED
        );
        List<Transaction> transactions = List.of(tx1, tx2, tx1);

        assertEquals(Map.of(first, 3), reports.countByAccount(transactions));
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(transactions);
        assertEquals(1, totals.size());
        assertEquals(40.4, totals.get(first), 0.000001);
    }

    @Test
    void equalAccountNumbersAreAggregatedIntoOneEntry() {
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");
        List<Transaction> transactions = List.of(
            new Transaction(1L, TransactionType.TRANSFER, stored, 100.5, TransactionStatus.SUCCESS),
            new Transaction(2L, TransactionType.TRANSFER, lookup, 200.25, TransactionStatus.SUCCESS)
        );

        assertNotSame(stored, lookup);
        assertEquals(Map.of(stored, 2), reports.countByAccount(transactions));
        assertEquals(Map.of(lookup, 300.75), reports.totalAmountByAccount(transactions));
    }
}
