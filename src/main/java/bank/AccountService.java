package bank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class AccountService {
    private static final Comparator<BankAccount> BY_NUMBER = Comparator.comparing(
        BankAccount::getId,
        Comparator.nullsLast(Comparator.comparing(AccountNumber::value))
    );

    private final Repository<AccountNumber, BankAccount> accounts;

    public AccountService(Repository<AccountNumber, BankAccount> accounts) {
        this.accounts = accounts;
    }

    public void open(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        accounts.save(account);
    }

    public Optional<BankAccount> find(AccountNumber number) {
        return Optional.ofNullable(accounts.findById(number));
    }

    public boolean close(AccountNumber number) {
        return accounts.deleteById(number);
    }

    public List<BankAccount> findAccounts(Predicate<? super BankAccount> condition) {
        return CollectionFunctions.filter(accounts.findAll(), condition);
    }

    public List<BankAccount> findRichAccounts(double minBalance) {
        return accounts.findAll().stream()
            .filter(account -> account.getBalance() >= minBalance)
            .toList();
    }

    public Optional<BankAccount> findFirstRichAccount(double minBalance) {
        return accounts.findAll().stream()
            .filter(account -> account.getBalance() >= minBalance)
            .findFirst();
    }

    public List<BankAccount> findDebitAccounts() {
        return accounts.findAll().stream()
            .filter(DebitAccount.class::isInstance)
            .toList();
    }

    public List<BankAccount> findAccountsByOwner(String owner) {
        return accounts.findAll().stream()
            .filter(account -> Objects.equals(account.getOwner(), owner))
            .toList();
    }

    public List<BankAccount> sortedAccounts(Comparator<? super BankAccount> comparator) {
        List<BankAccount> result = new ArrayList<>(accounts.findAll());
        result.sort(comparator);
        return result;
    }

    public List<BankAccount> richestFirst() {
        return accounts.findAll().stream()
            .sorted(Comparator.comparingDouble(BankAccount::getBalance).reversed())
            .toList();
    }

    public List<BankAccount> sortedByNumber() {
        return accounts.findAll().stream()
            .sorted(BY_NUMBER)
            .toList();
    }

    public List<BankAccount> sortedByTypeThenBalance() {
        Comparator<BankAccount> byTypeThenBalance = Comparator
            .comparing((BankAccount account) -> account.getClass().getSimpleName())
            .thenComparingDouble(BankAccount::getBalance);
        return accounts.findAll().stream()
            .sorted(byTypeThenBalance)
            .toList();
    }

    public List<BankAccount> sortedByOwnerThenNumber() {
        Comparator<BankAccount> byOwnerThenNumber = Comparator
            .comparing(BankAccount::getOwner, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(BY_NUMBER);
        return accounts.findAll().stream()
            .sorted(byOwnerThenNumber)
            .toList();
    }
}
