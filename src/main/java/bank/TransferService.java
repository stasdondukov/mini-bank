package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy, NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0 || from == to) {
            return false;
        }
        double commission = commissionPolicy.calculate(amount);
        double total = amount + commission;
        try {
            from.withdraw(total);
            to.deposit(amount);
            
            try {
                notificationService.notify("Transfer " + amount + " completed");
            } catch (Exception e) {
                System.err.println("Notification failed: " + e.getMessage());
            }
            
            return true;
        } catch (InsufficientFundsException | IllegalArgumentException | InvalidAmountException e) {
            return false;
        }
    }
}
