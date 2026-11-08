package bank;

public class ConsoleNotificationAdapter implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}
