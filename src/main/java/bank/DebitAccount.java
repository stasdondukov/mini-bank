package bank;

public class DebitAccount extends BankAccount {

    public DebitAccount(String number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (getBalance() >= amount) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }
}
