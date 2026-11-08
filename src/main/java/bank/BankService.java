package bank;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class BankService {
    private final AccountService accountService;
    private final TransferApplicationService transferApplicationService;
    private final BankReportService reportService;

    public BankService(Repository<AccountNumber, BankAccount> accounts, TransferService transferService) {
        this(new AccountRepositoryAdapter(accounts), transferService);
    }

    private BankService(AccountRepository accounts, TransferService transferService) {
        this(
            new AccountService(accounts),
            new TransferApplicationService(accounts, transferService),
            new BankReportService(accounts, number -> transferService.isAccountBlocked(number))
        );
    }

    public BankService(AccountService accountService, TransferApplicationService transferApplicationService,
                       BankReportService reportService) {
        this.accountService = accountService;
        this.transferApplicationService = transferApplicationService;
        this.reportService = reportService;
    }

    public void openAccount(BankAccount account) {
        accountService.open(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        return accountService.find(number).orElse(null);
    }

    public List<BankAccount> findAccounts(Predicate<? super BankAccount> condition) {
        return accountService.findAccounts(condition);
    }

    public List<BankAccount> findRichAccounts(double minBalance) {
        return accountService.findRichAccounts(minBalance);
    }

    public Optional<BankAccount> findFirstRichAccount(double minBalance) {
        return accountService.findFirstRichAccount(minBalance);
    }

    public double totalBalance() {
        return reportService.totalBalance();
    }

    public OptionalDouble averageBalance() {
        return reportService.averageBalance();
    }

    public Optional<BankAccount> richestAccount() {
        return reportService.richestAccount();
    }

    public List<BankAccount> findDebitAccounts() {
        return accountService.findDebitAccounts();
    }

    public List<BankAccount> findAccountsByOwner(String owner) {
        return accountService.findAccountsByOwner(owner);
    }

    public boolean hasBlockedAccounts() {
        return reportService.hasBlockedAccounts();
    }

    public boolean allAccountsPositive() {
        return reportService.allAccountsPositive();
    }

    public boolean noCreditAccounts() {
        return reportService.noCreditAccounts();
    }

    public List<AccountSummary> getAccountSummaries() {
        return reportService.getAccountSummaries();
    }

    public List<BankAccount> sortedAccounts(Comparator<? super BankAccount> comparator) {
        return accountService.sortedAccounts(comparator);
    }

    public List<BankAccount> richestFirst() {
        return accountService.richestFirst();
    }

    public List<BankAccount> sortedByNumber() {
        return accountService.sortedByNumber();
    }

    public List<BankAccount> sortedByTypeThenBalance() {
        return accountService.sortedByTypeThenBalance();
    }

    public List<BankAccount> sortedByOwnerThenNumber() {
        return accountService.sortedByOwnerThenNumber();
    }

    public boolean closeAccount(AccountNumber number) {
        return accountService.close(number);
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        transferApplicationService.transfer(fromNumber, toNumber, amount);
    }
}
