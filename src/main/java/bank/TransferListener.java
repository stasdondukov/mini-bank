package bank;

@FunctionalInterface
public interface TransferListener {
    void onTransferCompleted(Transaction transaction);
}
