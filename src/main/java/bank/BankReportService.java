package bank;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class BankReportService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final Predicate<AccountNumber> isBlocked;

    public BankReportService(Repository<AccountNumber, BankAccount> accounts, Predicate<AccountNumber> isBlocked) {
        this.accounts = accounts;
        this.isBlocked = isBlocked;
    }

    public double totalBalance() {
        return accounts.findAll().stream()
            .mapToDouble(BankAccount::getBalance)
            .sum();
    }

    public OptionalDouble averageBalance() {
        return accounts.findAll().stream()
            .mapToDouble(BankAccount::getBalance)
            .average();
    }

    public Optional<BankAccount> richestAccount() {
        return accounts.findAll().stream()
            .max(Comparator.comparingDouble(BankAccount::getBalance));
    }

    public boolean hasBlockedAccounts() {
        return accounts.findAll().stream()
            .anyMatch(account -> isBlocked.test(account.getNumber()));
    }

    public boolean allAccountsPositive() {
        return accounts.findAll().stream()
            .allMatch(account -> account.getBalance() > 0);
    }

    public boolean noCreditAccounts() {
        return accounts.findAll().stream()
            .noneMatch(CreditAccount.class::isInstance);
    }

    public List<AccountSummary> getAccountSummaries() {
        return accounts.findAll().stream()
            .map(this::toSummary)
            .toList();
    }

    private AccountSummary toSummary(BankAccount account) {
        return new AccountSummary(
            account.getId(),
            account.getClass().getSimpleName(),
            account.getBalance()
        );
    }
}
