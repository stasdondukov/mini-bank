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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ConcurrentBankDemo {
    private static final int OPERATIONS_PER_CLIENT = 2_000;

    public static void main(String[] args) {
        boolean synchronizedTransfers = args.length == 0 || !args[0].equals("--unsafe");
        List<AccountNumber> numbers = List.of(
            new AccountNumber("0000000001"),
            new AccountNumber("0000000002"),
            new AccountNumber("0000000003"),
            new AccountNumber("0000000004"),
            new AccountNumber("0000000005")
        );
        AccountRepository accounts = new InMemoryAccountRepository();
        for (int i = 0; i < numbers.size(); i++) {
            accounts.save(new DebitAccount(numbers.get(i), String.valueOf((char) ('A' + i)), 100_000));
        }

        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransferService transferService = new TransferService(
            new NoCommission(), new TransferEventPublisher(), blockedAccounts, new TransactionJournal()
        );
        ConcurrentBank bank = new ConcurrentBank(
            new TransferApplicationService(accounts, transferService),
            new BankReportService(accounts, blockedAccounts::isBlocked),
            synchronizedTransfers
        );
        double initialTotal = bank.totalBalance();
        System.out.println("Synchronized transfers: " + synchronizedTransfers);
        System.out.printf(Locale.ROOT, "INITIAL_TOTAL = %.0f%n", initialTotal);

        List<BankClient> clients = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numbers.size(); i++) {
            AccountNumber from = numbers.get(i);
            AccountNumber to = numbers.get((i + 1) % numbers.size());
            BankClient client = new BankClient(bank, from, to, OPERATIONS_PER_CLIENT);
            clients.add(client);
            threads.add(new Thread(client, "Client " + (i + 1)));
        }

        synchronized (bank) {
            for (Thread thread : threads) {
                thread.start();
            }
        }
        waitForClients(threads);
        double finalTotal = bank.totalBalance();

        for (int i = 0; i < clients.size(); i++) {
            BankClient client = clients.get(i);
            System.out.printf("%s: %d completed, %d rejected, %d errors%n",
                threads.get(i).getName(), client.succeeded(), client.rejected(), client.errors());
        }

        List<AccountSummary> balances = bank.snapshot().stream()
            .sorted(Comparator.comparing(account -> account.number().value()))
            .toList();
        for (int i = 0; i < balances.size(); i++) {
            AccountSummary account = balances.get(i);
            System.out.printf(Locale.ROOT, "Account %c (%s) = %.0f%n",
                'A' + i, account.number().value(), account.balance());
        }
        System.out.printf(Locale.ROOT, "FINAL_TOTAL = %.0f%n", finalTotal);
        System.out.println("INVARIANT = " + (initialTotal == finalTotal));
    }

    private static void waitForClients(List<Thread> threads) {
        boolean interrupted = false;
        for (Thread thread : threads) {
            while (thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    interrupted = true;
                    for (Thread client : threads) {
                        client.interrupt();
                    }
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
