package bank;

public final class DefaultAccountFactory implements AccountFactory {
    @Override
    public BankAccount create(AccountType type, AccountNumber number, String owner, double initialBalance) {
        return switch (type) {
            case DEBIT -> new DebitAccount(number, owner, initialBalance);
            case SAVINGS -> new SavingsAccount(number, owner, initialBalance, 1000);
            case CREDIT -> new CreditAccount(number, owner, initialBalance, 50_000);
        };
    }
}
