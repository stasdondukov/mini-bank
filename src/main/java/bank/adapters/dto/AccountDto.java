package bank.adapters.dto;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;

public record AccountDto(String number, String owner, double balance, String type) {
    public static AccountDto fromDomain(BankAccount account) {
        AccountNumber number = account.getId();
        return new AccountDto(
            number == null ? null : number.value(),
            account.getOwner(),
            account.getBalance(),
            account.getClass().getSimpleName()
        );
    }
}
