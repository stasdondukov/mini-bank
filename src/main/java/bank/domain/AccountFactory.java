package bank.domain;

public interface AccountFactory {
    BankAccount create(AccountType type, AccountNumber number, String owner, double initialBalance);
}
