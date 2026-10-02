package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            return false;
        }
        if (from == to) {
            return false;
        }
        double commission = commissionPolicy.calculate(amount);
        double total = amount + commission;
        if (from.withdraw(total)) {
            to.deposit(amount);
            notificationService.notify("Transfer " + amount + " completed");
            return true;
        }
        return false;
    }
}
