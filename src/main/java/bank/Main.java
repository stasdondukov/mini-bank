package bank;

public class Main {

    public static void main(String[] args) {
        CommissionPolicy noCommission = amount -> 0.0;
        NotificationService console = new ConsoleNotificationAdapter();
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransactionJournal journal = new TransactionJournal();
        TransferService transferService = new TransferService(
            noCommission, console, blockedAccounts, journal
        );

        AccountRepository accounts = new InMemoryAccountRepository();
        BankService bank = new BankService(
            new AccountService(accounts),
            new TransferApplicationService(accounts, transferService),
            new BankReportService(accounts, blockedAccounts::isBlocked)
        );
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
