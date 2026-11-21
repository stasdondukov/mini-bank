package bank.ports;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

import java.util.Optional;

@FunctionalInterface
public interface AccountLookup {
    Optional<BankAccount> findById(AccountNumber number);
}
