package bank.application;

import bank.domain.AccountNumber;

public record TransferRequest(
    AccountNumber from,
    AccountNumber to,
    double amount) {
}
