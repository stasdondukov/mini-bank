package bank;

import bank.domain.AccountNumber;
import bank.domain.InsufficientFundsException;
import bank.domain.TransferLimitExceededException;

final class BankClient implements Runnable {
    private final ConcurrentBank bank;
    private final AccountNumber from;
    private final AccountNumber to;
    private final int operations;
    private int succeeded;
    private int rejected;

    BankClient(ConcurrentBank bank, AccountNumber from, AccountNumber to, int operations) {
        this.bank = bank;
        this.from = from;
        this.to = to;
        this.operations = operations;
    }

    @Override
    public void run() {
        for (int operation = 0;
             operation < operations && !Thread.currentThread().isInterrupted();
             operation++) {
            int amount = operation % 250 == 0 ? 50_001 : operation % 100 + 1;
            try {
                bank.transfer(from, to, amount);
                succeeded++;
            } catch (InsufficientFundsException | TransferLimitExceededException e) {
                rejected++;
            }
        }
    }

    int succeeded() {
        return succeeded;
    }

    int rejected() {
        return rejected;
    }
}
