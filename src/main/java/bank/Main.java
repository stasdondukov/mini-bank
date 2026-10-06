package bank;

public class Main {

    public static void main(String[] args) {
        CommissionPolicy commissionPolicy = new NoCommission();
        NotificationService notificationService = new ConsoleNotificationService();
        TransferService transferService = new TransferService(commissionPolicy, notificationService);

        BankAccount from = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 1000.0);
        BankAccount to = new DebitAccount(new AccountNumber("0987654321"), "Olga", 2000.0);

        try {
            transferService.transfer(from, to, 5000.0);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (TransferLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}
