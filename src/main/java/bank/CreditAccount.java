package bank;

public class CreditAccount extends BankAccount {
    private final double creditLimit;

    public CreditAccount(AccountNumber number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        this.creditLimit = creditLimit;
    }

    public CreditAccount(String number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        this.creditLimit = creditLimit;
    }

    @Override
    protected double getAvailableAmount() {
        return getBalance() + creditLimit;
    }
}
