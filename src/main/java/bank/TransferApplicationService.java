package bank;

import java.util.Optional;

public class TransferApplicationService {
    private final AccountRepository accounts;
    private final TransferService transferService;

    public TransferApplicationService(AccountRepository accounts, TransferService transferService) {
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(AccountNumber fromNumber, AccountNumber toNumber, double amount) {
        Optional<BankAccount> from = accounts.findById(fromNumber);
        Optional<BankAccount> to = accounts.findById(toNumber);
        transferService.transfer(
            from.orElseThrow(() -> new IllegalArgumentException("Source account not found")),
            to.orElseThrow(() -> new IllegalArgumentException("Destination account not found")),
            amount
        );
    }
}
