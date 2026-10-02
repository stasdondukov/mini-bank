package bank;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    static class TestNotificationService implements NotificationService {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void notify(String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    @Test
    void shouldTransferWithoutCommissionSuccessfully() {

        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TestNotificationService notificationService = new TestNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        boolean result = transferService.transfer(from, to, 3000.0);

        assertTrue(result);
        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
        assertEquals(1, notificationService.getMessages().size());
        assertEquals("Transfer 3000.0 completed", notificationService.getMessages().get(0));
    }

    @Test
    void shouldTransferWithPercentCommissionSuccessfully() {

        BankAccount from = new DebitAccount("1", "A", 11000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TestNotificationService notificationService = new TestNotificationService();
        TransferService transferService = new TransferService(new PercentCommission(1.0), notificationService);

        boolean result = transferService.transfer(from, to, 10000.0);

        assertTrue(result);
        assertEquals(900.0, from.getBalance()); 
        assertEquals(12000.0, to.getBalance());  
        assertEquals(1, notificationService.getMessages().size());
        assertEquals("Transfer 10000.0 completed", notificationService.getMessages().get(0));
    }

    @Test
    void shouldFailWhenSenderCannotCoverAmountWithCommission() {

        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TestNotificationService notificationService = new TestNotificationService();
        TransferService transferService = new TransferService(new PercentCommission(1.0), notificationService);

        boolean result = transferService.transfer(from, to, 10000.0);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
        assertTrue(notificationService.getMessages().isEmpty());
    }

    @Test
    void shouldFailWhenTransferringToSameAccount() {

        BankAccount from = new DebitAccount("1", "A", 10000.0);
        TestNotificationService notificationService = new TestNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        boolean result = transferService.transfer(from, from, 3000.0);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertTrue(notificationService.getMessages().isEmpty());
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {

        BankAccount from = new DebitAccount("1", "A", 10000.0);
        BankAccount to = new DebitAccount("2", "B", 2000.0);
        TestNotificationService notificationService = new TestNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        boolean resultZero = transferService.transfer(from, to, 0.0);
        boolean resultNegative = transferService.transfer(from, to, -100.0);

        assertFalse(resultZero);
        assertFalse(resultNegative);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
        assertTrue(notificationService.getMessages().isEmpty());
    }

    @Test
    void shouldWorkBetweenDifferentAccountTypes() {

        BankAccount from = new CreditAccount("3", "CreditUser", 500.0, 3000.0);
        BankAccount to = new SavingsAccount("4", "SavingsUser", 1000.0, 500.0);
        TestNotificationService notificationService = new TestNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        boolean result = transferService.transfer(from, to, 2000.0);

        assertTrue(result);
        assertEquals(-1500.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
        assertEquals(1, notificationService.getMessages().size());
    }
}
