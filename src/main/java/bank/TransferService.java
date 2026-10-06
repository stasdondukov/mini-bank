package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
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
        double commission = commissionPolicy.calculate(amount);
        double total = amount + commission;
        
        from.withdraw(total);
        to.deposit(amount);
        notificationService.notify("Transfer " + amount + " completed");
    }
}
