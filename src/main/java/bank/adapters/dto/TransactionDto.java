package bank.adapters.dto;

import bank.domain.AccountNumber;
import bank.domain.Transaction;
import bank.domain.TransactionType;

public record TransactionDto(long id, String from, String to, double amount, String status) {
    public static TransactionDto fromDomain(Transaction transaction) {
        String from = transaction.type() == TransactionType.DEPOSIT
            ? null : numberValue(transaction.account());
        String to = switch (transaction.type()) {
            case DEPOSIT -> numberValue(transaction.account());
            case TRANSFER -> numberValue(transaction.destination());
            case WITHDRAWAL -> null;
        };
        return new TransactionDto(transaction.id(), from, to, transaction.amount(), transaction.status().name());
    }

    private static String numberValue(AccountNumber number) {
        return number == null ? null : number.value();
    }
}
