package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;
    private final TransactionJournal journal;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService,
                           BlockedAccountRegistry blockedAccounts, TransactionJournal journal) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
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
        if (blockedAccounts.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }
        double commission = commissionPolicy.calculate(amount);
        double total = amount + commission;
        
        double fromBalance = from.getBalance();
        double toBalance = to.getBalance();
        try {
            from.withdraw(total);
            to.deposit(amount);
        } catch (RuntimeException e) {
            from.setBalance(fromBalance);
            to.setBalance(toBalance);
            throw e;
        }
        journal.add(new Transaction(
            journal.size() + 1L,
            TransactionType.TRANSFER,
            from.getNumber(),
            amount,
            TransactionStatus.SUCCESS
        ));
        notificationService.notify("Transfer " + amount + " completed");
    }
}
