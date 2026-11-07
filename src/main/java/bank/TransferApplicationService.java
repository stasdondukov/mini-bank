package bank;

public class TransferApplicationService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public TransferApplicationService(Repository<AccountNumber, BankAccount> accounts, TransferService transferService) {
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        BankAccount from = accounts.findById(fromNumber);
        BankAccount to = accounts.findById(toNumber);
        if (from == null) {
            throw new IllegalArgumentException("Source account not found");
        }
        if (to == null) {
            throw new IllegalArgumentException("Destination account not found");
        }
        transferService.transfer(from, to, amount);
    }
}
