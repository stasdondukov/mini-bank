package bank;

public class CreditAccount extends BankAccount {
    private final double creditLimit;

    public CreditAccount(String number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        this.creditLimit = creditLimit;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (getBalance() - amount >= -creditLimit) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
}
