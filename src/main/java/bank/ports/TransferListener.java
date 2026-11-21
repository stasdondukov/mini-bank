package bank.ports;

import bank.domain.Transaction;

@FunctionalInterface
public interface TransferListener {
    void onTransferCompleted(Transaction transaction);
}
