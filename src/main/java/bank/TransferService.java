package bank;

public class TransferService {

    public boolean transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            return false;
        }
        if (from == to) {
            return false;
        }
        if (from.withdraw(amount)) {
            to.deposit(amount);
            return true;
        }
        return false;
    }
}
