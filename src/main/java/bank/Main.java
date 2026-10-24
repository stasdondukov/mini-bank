package bank;

public class Main {

    public static void main(String[] args) {
        CommissionPolicy commissionPolicy = new NoCommission();
        NotificationService notificationService = new ConsoleNotificationService();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransactionJournal journal = new TransactionJournal();
        TransferService transferService = new TransferService(
            commissionPolicy, notificationService, blockedAccounts, journal
        );

        BankService bank = new BankService(new InMemoryRepository<>(), transferService);
        bank.openAccount(new DebitAccount("1234567890", "Ivan", 1000.0));
        bank.openAccount(new DebitAccount("0987654321", "Olga", 2000.0));

        try {
            bank.transfer(new AccountNumber("1234567890"), new AccountNumber("0987654321"), 500.0);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (TransferLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}
