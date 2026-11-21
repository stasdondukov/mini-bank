package bank.application;

import bank.domain.Transaction;
import bank.ports.TransferListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TransferEventPublisher {
    private final List<TransferListener> listeners = new ArrayList<>();

    public void subscribe(TransferListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void publish(Transaction transaction) {
        RuntimeException failure = null;
        for (TransferListener listener : List.copyOf(listeners)) {
            try {
                listener.onTransferCompleted(transaction);
            } catch (RuntimeException exception) {
                if (failure == null) {
                    failure = exception;
                } else if (failure != exception) {
                    failure.addSuppressed(exception);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }
}
