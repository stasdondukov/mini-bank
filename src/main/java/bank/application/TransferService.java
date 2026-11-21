package bank.application;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.CommissionPolicy;
import bank.domain.InvalidAmountException;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;
import bank.domain.TransactionType;
import bank.domain.TransferLimitExceededException;
import bank.ports.NotificationService;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final TransferEventPublisher events;
    private final BlockedAccountRegistry blockedAccounts;
    private final TransactionJournal journal;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService,
                           BlockedAccountRegistry blockedAccounts, TransactionJournal journal) {
        this(commissionPolicy, new TransferEventPublisher(), blockedAccounts, journal);
        events.subscribe(new TransferNotificationListener(notificationService));
    }

    public TransferService(CommissionPolicy commissionPolicy, TransferEventPublisher events,
                           BlockedAccountRegistry blockedAccounts, TransactionJournal journal) {
        this.commissionPolicy = commissionPolicy;
        this.events = events;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
    }

    public boolean isAccountBlocked(AccountNumber number) {
        return blockedAccounts.isBlocked(number);
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount > 50000) {
            throw new TransferLimitExceededException("Transfer limit exceeded");
        }
        if (isAccountBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }
        double commission = commissionPolicy.calculate(amount);
        from.transferTo(to, amount, commission);
        Transaction transaction = new Transaction(
            journal.size() + 1L,
            TransactionType.TRANSFER,
            from.getNumber(),
            amount,
            TransactionStatus.SUCCESS
        );
        journal.add(transaction);
        events.publish(transaction);
    }
}
