package bank;

public class BankService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public BankService(Repository<AccountNumber, BankAccount> accounts, TransferService transferService) {
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        accounts.save(account);
    }

    public BankAccount findAccount(AccountNumber number) {
        return accounts.findById(number);
    }

    public boolean closeAccount(AccountNumber number) {
        return accounts.deleteById(number);
    }
}
