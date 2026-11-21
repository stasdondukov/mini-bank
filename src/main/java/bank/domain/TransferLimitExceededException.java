package bank.domain;

public class TransferLimitExceededException extends RuntimeException {
    public TransferLimitExceededException(String message) {
        super(message);
    }
}
