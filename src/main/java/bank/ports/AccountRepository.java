package bank.ports;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

public interface AccountRepository extends AccountLookup, AccountListing {
    void save(BankAccount account);

    boolean deleteById(AccountNumber number);
}
