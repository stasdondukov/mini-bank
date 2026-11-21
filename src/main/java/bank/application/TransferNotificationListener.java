package bank.application;

import bank.domain.Transaction;
import bank.ports.NotificationService;
import bank.ports.TransferListener;

public final class TransferNotificationListener implements TransferListener {
    private final NotificationService notificationService;

    public TransferNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onTransferCompleted(Transaction transaction) {
        notificationService.notify("Transfer " + transaction.amount() + " completed");
    }
}
