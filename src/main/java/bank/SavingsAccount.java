package bank;

public class SavingsAccount extends BankAccount {
    private final double minimumBalance;

    public SavingsAccount(String number, String owner, double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        this.minimumBalance = minimumBalance;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (getBalance() - amount >= minimumBalance) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
}
