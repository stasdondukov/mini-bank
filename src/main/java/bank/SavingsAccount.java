package bank;

public class SavingsAccount extends BankAccount {
    private final double minimumBalance;

    public SavingsAccount(AccountNumber number, String owner, double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        this.minimumBalance = minimumBalance;
    }

    public SavingsAccount(String number, String owner, double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        this.minimumBalance = minimumBalance;
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance() - minimumBalance;
    }
}
