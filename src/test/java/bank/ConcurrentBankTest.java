package bank;

import bank.adapters.InMemoryAccountRepository;
import bank.application.AccountSummary;
import bank.application.BankReportService;
import bank.application.BlockedAccountRegistry;
import bank.application.TransactionJournal;
import bank.application.TransferApplicationService;
import bank.application.TransferEventPublisher;
import bank.application.TransferService;
import bank.domain.AccountNumber;
import bank.domain.DebitAccount;
import bank.domain.NoCommission;
import bank.ports.AccountRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ConcurrentBankTest {
    private final List<AccountNumber> numbers = List.of(
        new AccountNumber("0000000001"),
        new AccountNumber("0000000002"),
        new AccountNumber("0000000003"),
        new AccountNumber("0000000004"),
        new AccountNumber("0000000005")
    );
    private final TransactionJournal journal = new TransactionJournal();
    private final ConcurrentBank bank = createBank();

    @Test
    void clientContinuesAfterRejectedTransfer() throws InterruptedException {
        BankClient client = new BankClient(bank, numbers.get(0), numbers.get(1), 2);
        Thread thread = new Thread(client);
        thread.start();
        thread.join(5_000);

        assertFalse(thread.isAlive());
        assertEquals(1, client.rejected());
        assertEquals(1, client.succeeded());
        assertEquals(1, journal.size());
        assertEquals(500_000.0, totalBalance());
    }

    @Test
    void concurrentTransfersPreserveTotalInEverySnapshot() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numbers.size(); i++) {
            AccountNumber from = numbers.get(i);
            AccountNumber to = numbers.get((i + 1) % numbers.size());
            threads.add(new Thread(new BankClient(bank, from, to, 2_000)));
        }

        synchronized (bank) {
            for (Thread thread : threads) {
                thread.start();
            }
        }
        try {
            for (int i = 0; i < 100; i++) {
                assertEquals(500_000.0, totalBalance());
            }
            for (Thread thread : threads) {
                thread.join(5_000);
                assertFalse(thread.isAlive());
            }

            assertEquals(9_960, journal.size());
            assertEquals(500_000.0, totalBalance());
        } finally {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
    }

    private ConcurrentBank createBank() {
        AccountRepository accounts = new InMemoryAccountRepository();
        for (AccountNumber number : numbers) {
            accounts.save(new DebitAccount(number, "Owner", 100_000));
        }
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService service = new TransferService(
            new NoCommission(), new TransferEventPublisher(), blockedAccounts, journal
        );
        return new ConcurrentBank(
            new TransferApplicationService(accounts, service),
            new BankReportService(accounts, blockedAccounts::isBlocked)
        );
    }

    private double totalBalance() {
        return bank.snapshot().stream().mapToDouble(AccountSummary::balance).sum();
    }
}
