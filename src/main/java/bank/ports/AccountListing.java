package bank.ports;

import bank.domain.BankAccount;

import java.util.List;

@FunctionalInterface
public interface AccountListing {
    List<BankAccount> findAll();
}
