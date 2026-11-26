package bank;

import bank.domain.BankAccount;
import bank.domain.CreditAccount;
import bank.domain.DebitAccount;
import bank.domain.InsufficientFundsException;
import bank.domain.SavingsAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadSafeAccountTest {
    @Test
    void concurrentDepositsAndWithdrawalsPreserveBalance() throws InterruptedException {
        BankAccount account = new DebitAccount("0000000001", "A", 20_000);
        AtomicInteger completed = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        for (int client = 0; client < 3; client++) {
            threads.add(new Thread(() -> {
                for (int i = 0; i < 5_000; i++) {
                    account.deposit(2);
                    completed.incrementAndGet();
                }
            }));
            threads.add(new Thread(() -> {
                for (int i = 0; i < 5_000; i++) {
                    account.withdraw(1);
                    completed.incrementAndGet();
                }
            }));
        }

        runThreads(account, threads);

        assertEquals(30_000, completed.get());
        assertEquals(35_000.0, account.getBalance());
    }

    @ParameterizedTest
    @MethodSource("accounts")
    void concurrentWithdrawalsRespectAccountLimits(BankAccount account, double finalBalance)
            throws InterruptedException {
        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        for (int client = 0; client < 5; client++) {
            threads.add(new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    try {
                        account.withdraw(1);
                        succeeded.incrementAndGet();
                    } catch (InsufficientFundsException e) {
                        rejected.incrementAndGet();
                    }
                }
            }));
        }

        runThreads(account, threads);

        assertEquals(250, succeeded.get());
        assertEquals(250, rejected.get());
        assertEquals(finalBalance, account.getBalance());
    }

    @Test
    void readingLockedAccountDoesNotBlockAnotherAccount() throws InterruptedException {
        BankAccount first = new DebitAccount("0000000001", "A", 100);
        BankAccount second = new DebitAccount("0000000002", "B", 200);
        AtomicInteger observedBalance = new AtomicInteger();
        Thread reader = new Thread(() -> observedBalance.set((int) first.getBalance()));
        Thread otherClient = new Thread(() -> second.deposit(10));

        synchronized (first) {
            reader.start();
            otherClient.start();
            otherClient.join(5_000);
            assertFalse(otherClient.isAlive());
            assertEquals(210.0, second.getBalance());

            reader.join(100);
            assertTrue(reader.isAlive());
            first.deposit(5);
        }

        reader.join(5_000);
        assertFalse(reader.isAlive());
        assertEquals(105, observedBalance.get());
    }

    private static Stream<Arguments> accounts() {
        return Stream.of(
            Arguments.of(new DebitAccount("0000000001", "A", 250), 0.0),
            Arguments.of(new SavingsAccount("0000000002", "B", 350, 100), 100.0),
            Arguments.of(new CreditAccount("0000000003", "C", 150, 100), -100.0)
        );
    }

    private void runThreads(BankAccount account, List<Thread> threads) throws InterruptedException {
        synchronized (account) {
            for (Thread thread : threads) {
                thread.start();
            }
        }
        for (Thread thread : threads) {
            thread.join(5_000);
            assertFalse(thread.isAlive());
        }
    }
}
