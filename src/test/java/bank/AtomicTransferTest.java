package bank;

import bank.application.BlockedAccountRegistry;
import bank.application.TransactionJournal;
import bank.application.TransferEventPublisher;
import bank.application.TransferService;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import bank.domain.NoCommission;
import bank.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtomicTransferTest {
    @Test
    void oppositeTransfersFinishAndPreserveBothBalancesAndJournal() throws InterruptedException {
        BankAccount first = new DebitAccount("0000000001", "A", 10_000);
        BankAccount second = new DebitAccount("0000000002", "B", 10_000);
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new NoCommission(), new TransferEventPublisher(), new BlockedAccountRegistry(), journal
        );
        Thread forward = worker(() -> {
            for (int i = 0; i < 5_000; i++) {
                service.transfer(first, second, 1);
            }
        });
        Thread backward = worker(() -> {
            for (int i = 0; i < 5_000; i++) {
                service.transfer(second, first, 1);
            }
        });

        forward.start();
        backward.start();
        join(forward);
        join(backward);

        assertEquals(10_000.0, first.getBalance());
        assertEquals(10_000.0, second.getBalance());
        List<Transaction> transactions = journal.findAll();
        assertEquals(10_000, transactions.size());
        assertEquals(10_000L, transactions.stream().map(Transaction::id).distinct().count());
        assertEquals(5_000, journal.findByAccount(first.getNumber()).size());
        assertEquals(5_000, journal.findByAccount(second.getNumber()).size());
    }

    @Test
    void bothAccountsAreLockedUntilTransferCompletes() throws InterruptedException {
        BankAccount source = new DebitAccount("0000000001", "A", 100);
        PausingAccount destination = new PausingAccount(false);
        BankAccount third = new DebitAccount("0000000003", "C", 100);
        BankAccount fourth = new DebitAccount("0000000004", "D", 200);

        AtomicInteger sourceBalance = new AtomicInteger(-1);
        AtomicInteger destinationBalance = new AtomicInteger(-1);
        Thread transfer = worker(() -> source.transferTo(destination, 20, 0));
        Thread sourceReader = worker(() -> sourceBalance.set((int) source.getBalance()));
        Thread destinationReader = worker(() -> destinationBalance.set((int) destination.getBalance()));
        Thread independentTransfer = worker(() -> third.transferTo(fourth, 10, 0));

        transfer.start();
        try {
            awaitPause(destination);
            sourceReader.start();
            destinationReader.start();
            independentTransfer.start();

            awaitBlocked(sourceReader);
            awaitBlocked(destinationReader);
            assertEquals(-1, sourceBalance.get());
            assertEquals(-1, destinationBalance.get());
            join(independentTransfer);
            assertEquals(90.0, third.getBalance());
            assertEquals(210.0, fourth.getBalance());
        } finally {
            destination.release();
        }

        join(transfer);
        join(sourceReader);
        join(destinationReader);
        assertEquals(80, sourceBalance.get());
        assertEquals(220, destinationBalance.get());
    }

    @Test
    void rollbackDoesNotOverwriteAnotherClientsDeposit() throws InterruptedException {
        BankAccount source = new DebitAccount("0000000001", "A", 100);
        PausingAccount destination = new PausingAccount(true);
        AtomicInteger failures = new AtomicInteger();
        Thread transfer = worker(() -> {
            try {
                source.transferTo(destination, 20, 0);
            } catch (IllegalStateException e) {
                failures.incrementAndGet();
            }
        });
        Thread deposit = worker(() -> source.deposit(5));

        transfer.start();
        try {
            awaitPause(destination);
            deposit.start();
            awaitBlocked(deposit);
        } finally {
            destination.release();
        }

        join(transfer);
        join(deposit);
        assertEquals(1, failures.get());
        assertEquals(105.0, source.getBalance());
        assertEquals(200.0, destination.getBalance());
    }

    private static Thread worker(Runnable action) {
        Thread thread = new Thread(action);
        thread.setDaemon(true);
        return thread;
    }

    private static void join(Thread thread) throws InterruptedException {
        thread.join(5_000);
        assertFalse(thread.isAlive(), "Thread did not finish");
    }

    private static void awaitPause(PausingAccount account) {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (!account.isPaused() && System.nanoTime() < deadline) {
            Thread.yield();
        }
        assertTrue(account.isPaused(), "Transfer did not reach deposit");
    }

    private static void awaitBlocked(Thread thread) {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (thread.isAlive() && thread.getState() != Thread.State.BLOCKED
                && System.nanoTime() < deadline) {
            Thread.yield();
        }
        assertEquals(Thread.State.BLOCKED, thread.getState());
    }

    private static class PausingAccount extends DebitAccount {
        private final boolean failing;
        private volatile boolean paused;
        private volatile boolean released;

        PausingAccount(boolean failing) {
            super("0000000002", "B", 200);
            this.failing = failing;
        }

        @Override
        public void deposit(double amount) {
            paused = true;
            while (!released) {
                Thread.yield();
            }
            super.deposit(amount);
            if (failing) {
                throw new IllegalStateException("Deposit failed");
            }
        }

        boolean isPaused() {
            return paused;
        }

        void release() {
            released = true;
        }
    }
}
