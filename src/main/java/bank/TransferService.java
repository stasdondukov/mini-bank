package bank;

public class TransferService {
    private final CommissionPolicy commissionPolicy;

    public TransferService(CommissionPolicy commissionPolicy) {
        this.commissionPolicy = commissionPolicy;
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
            return true;
        }
        return false;
    }
}
