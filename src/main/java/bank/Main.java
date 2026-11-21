package bank;

public class Main {

    public static void main(String[] args) {
        CommissionPolicy noCommission = amount -> 0.0;
        NotificationService notification = new LoggingNotificationDecorator(new ConsoleNotificationAdapter());
        TransferEventPublisher events = new TransferEventPublisher();
        events.subscribe(new TransferNotificationListener(notification));
        events.subscribe(transaction -> System.out.println("Transfer completed"));
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        TransactionJournal journal = new TransactionJournal();
        TransferService transferService = new TransferService(
            noCommission, events, blockedAccounts, journal
        );

        AccountRepository accounts = new InMemoryAccountRepository();
        BankService bank = new BankService(
            new AccountService(accounts),
            new TransferApplicationService(accounts, transferService),
            new BankReportService(accounts, blockedAccounts::isBlocked)
        );
        AccountFactory factory = new DefaultAccountFactory();
        AccountNumber from = new AccountNumber("1234567890");
        AccountNumber to = new AccountNumber("0987654321");
        bank.openAccount(factory.create(AccountType.DEBIT, from, "Ivan", 1000.0));
        bank.openAccount(factory.create(AccountType.DEBIT, to, "Olga", 2000.0));

        try {
            bank.transfer(from, to, 500.0);
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
