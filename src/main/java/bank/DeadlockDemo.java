package bank;

import bank.application.BlockedAccountRegistry;
import bank.application.TransactionJournal;
import bank.application.TransferEventPublisher;
import bank.application.TransferService;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import bank.domain.NoCommission;

import java.util.concurrent.atomic.AtomicInteger;

public class DeadlockDemo {
    private static final int OPERATIONS = 100_000;

    public static void main(String[] args) throws InterruptedException {
        BankAccount a = new DebitAccount("0000000001", "A", 100_000);
        BankAccount b = new DebitAccount("0000000002", "B", 100_000);

        if (args.length > 0 && args[0].equals("--deadlock")) {
            demonstrateDeadlock(a, b);
        } else {
            runOppositeTransfers(a, b);
        }
    }

    private static void runOppositeTransfers(BankAccount a, BankAccount b)
            throws InterruptedException {
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new NoCommission(), new TransferEventPublisher(), new BlockedAccountRegistry(), journal
        );
        double initialTotal = a.getBalance() + b.getBalance();
        AtomicInteger ready = new AtomicInteger();
        Thread first = new Thread(() -> transferRepeatedly(service, a, b, ready), "Thread 1");
        Thread second = new Thread(() -> transferRepeatedly(service, b, a, ready), "Thread 2");

        first.start();
        second.start();
        first.join();
        second.join();

        double finalTotal = a.getBalance() + b.getBalance();
        System.out.println("Thread 1 state: " + first.getState());
        System.out.println("Thread 2 state: " + second.getState());
        System.out.println("COMPLETED = " + journal.size());
        System.out.println("INITIAL_TOTAL = " + initialTotal);
        System.out.println("FINAL_TOTAL = " + finalTotal);
        System.out.println("INVARIANT = " + (initialTotal == finalTotal));
        if (journal.size() != OPERATIONS * 2 || initialTotal != finalTotal) {
            throw new IllegalStateException("Transfer experiment failed");
        }
    }

    private static void transferRepeatedly(TransferService service, BankAccount from,
                                           BankAccount to, AtomicInteger ready) {
        ready.incrementAndGet();
        waitForStep(ready, 2);
        for (int i = 0; i < OPERATIONS; i++) {
            service.transfer(from, to, 1);
        }
    }

    private static void demonstrateDeadlock(BankAccount a, BankAccount b) {
        AtomicInteger step = new AtomicInteger();

        Thread first = new Thread(() -> {
            synchronized (a) {
                System.out.println("Thread 1 locked A");
                step.set(1);
                waitForStep(step, 2);
                System.out.println("Thread 1 waits for B");
                step.set(3);
                synchronized (b) {
                    a.withdraw(1);
                    b.deposit(1);
                }
            }
        }, "Thread 1");

        Thread second = new Thread(() -> {
            waitForStep(step, 1);
            synchronized (b) {
                System.out.println("Thread 2 locked B");
                step.set(2);
                waitForStep(step, 3);
                System.out.println("Thread 2 waits for A");
                synchronized (a) {
                    b.withdraw(1);
                    a.deposit(1);
                }
            }
        }, "Thread 2");

        first.setDaemon(true);
        second.setDaemon(true);
        first.start();
        second.start();

        long deadline = System.nanoTime() + 5_000_000_000L;
        while (System.nanoTime() < deadline && !bothBlocked(first, second)) {
            Thread.yield();
        }
        System.out.println("Thread 1 state: " + first.getState());
        System.out.println("Thread 2 state: " + second.getState());
        boolean deadlock = bothBlocked(first, second);
        System.out.println("DEADLOCK = " + deadlock);

        if (!deadlock) {
            throw new IllegalStateException("Deadlock was not reproduced");
        }
    }

    private static void waitForStep(AtomicInteger step, int expected) {
        while (step.get() < expected) {
            Thread.yield();
        }
    }

    private static boolean bothBlocked(Thread first, Thread second) {
        return first.getState() == Thread.State.BLOCKED
            && second.getState() == Thread.State.BLOCKED;
    }
}
