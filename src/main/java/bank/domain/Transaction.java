package bank.domain;

public record Transaction(
    Long id,
    TransactionType type,
    AccountNumber account,
    AccountNumber destination,
    double amount,
    TransactionStatus status) implements Identifiable<Long> {

    public Transaction(Long id, TransactionType type, AccountNumber account, double amount, TransactionStatus status) {
        this(id, type, account, null, amount, status);
    }

    @Override
    public Long getId() {
        return id;
    }
}
