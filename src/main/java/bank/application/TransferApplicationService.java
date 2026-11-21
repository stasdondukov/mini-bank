package bank.application;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.ports.AccountLookup;

import java.util.Optional;

public class TransferApplicationService {
    private final AccountLookup accounts;
    private final TransferService transferService;

    public TransferApplicationService(AccountLookup accounts, TransferService transferService) {
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
