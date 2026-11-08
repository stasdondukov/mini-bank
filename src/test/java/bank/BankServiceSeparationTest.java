package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceSeparationTest {

    @Test
    void accountLifecycleNeedsOnlyRepository() {
        AccountService service = new AccountService(new AccountRepositoryAdapter(new InMemoryRepository<>()));
        AccountNumber number = new AccountNumber("1111111111");
        BankAccount account = new DebitAccount(number, "Ivan", 1000);

        assertTrue(service.find(number).isEmpty());
        service.open(account);
        assertSame(account, service.find(number).orElseThrow());
        assertTrue(service.close(number));
        assertTrue(service.find(number).isEmpty());
    }

    @Test
    void transferWorksWithAnIndependentAccountRepository() {
        AccountRepository accounts = new AccountRepository() {
            private final Map<AccountNumber, BankAccount> values = new HashMap<>();

            @Override
            public Optional<BankAccount> findById(AccountNumber number) {
                return Optional.ofNullable(values.get(number));
            }

            @Override
            public void save(BankAccount account) {
                values.put(account.getNumber(), account);
            }

            @Override
            public boolean deleteById(AccountNumber number) {
                throw new AssertionError("A transfer must not delete an account");
            }

            @Override
            public List<BankAccount> findAll() {
                throw new AssertionError("A transfer must not request all accounts");
            }
        };
        BankAccount source = new DebitAccount("1111111111", "Ivan", 2000);
        BankAccount destination = new DebitAccount("2222222222", "Olga", 500);
        accounts.save(source);
        accounts.save(destination);

        TransactionJournal journal = new TransactionJournal();
        List<String> notifications = new ArrayList<>();
        TransferService rules = new TransferService(
            amount -> amount * 0.01, notifications::add, new BlockedAccountRegistry(), journal
        );
        TransferApplicationService service = new TransferApplicationService(accounts, rules);

        service.transfer(source.getNumber(), destination.getNumber(), 1000);

        assertEquals(990, source.getBalance());
        assertEquals(1500, destination.getBalance());
        assertEquals(1, journal.size());
        assertEquals(List.of("Transfer 1000.0 completed"), notifications);
    }

    @Test
    void reportsReadAccountAndBlockingDataWithoutTransferService() {
        AccountRepository accounts = new AccountRepositoryAdapter(new InMemoryRepository<>());
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        BankReportService reports = new BankReportService(accounts, blockedAccounts::isBlocked);
        BankAccount account = new DebitAccount("1111111111", "Ivan", 1000);
        accounts.save(account);

        assertEquals(1000, reports.totalBalance());
        assertFalse(reports.hasBlockedAccounts());
        blockedAccounts.block(account.getNumber());
        assertTrue(reports.hasBlockedAccounts());
    }
}
