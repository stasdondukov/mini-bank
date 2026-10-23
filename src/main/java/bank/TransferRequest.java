package bank;

public record TransferRequest(
    AccountNumber from,
    AccountNumber to,
    double amount) {
}
