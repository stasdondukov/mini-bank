package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService,
                           BlockedAccountRegistry blockedAccounts) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
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
        
        from.withdraw(total);
        to.deposit(amount);
        notificationService.notify("Transfer " + amount + " completed");
    }
}
