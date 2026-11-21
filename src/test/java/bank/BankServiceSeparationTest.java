package bank;

import bank.adapters.AccountRepositoryAdapter;
import bank.adapters.InMemoryRepository;
import bank.application.AccountService;
import bank.application.BankReportService;
import bank.application.BlockedAccountRegistry;
import bank.application.TransactionJournal;
import bank.application.TransferApplicationService;
import bank.application.TransferService;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.DebitAccount;
import bank.ports.AccountListing;
import bank.ports.AccountLookup;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    void transferWorksWithLookupOnly() {
        BankAccount source = new DebitAccount("1111111111", "Ivan", 2000);
        BankAccount destination = new DebitAccount("2222222222", "Olga", 500);
        Map<AccountNumber, BankAccount> values = Map.of(
            source.getNumber(), source,
            destination.getNumber(), destination
        );
        AccountLookup accounts = number -> Optional.ofNullable(values.get(number));

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
    void reportsWorkWithListingOnlyAndUseCurrentBlockingData() {
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        BankAccount account = new DebitAccount("1111111111", "Ivan", 1000);
        AccountListing accounts = () -> List.of(account);
        BankReportService reports = new BankReportService(accounts, blockedAccounts::isBlocked);

        assertEquals(1000, reports.totalBalance());
        assertFalse(reports.hasBlockedAccounts());
        blockedAccounts.block(account.getNumber());
        assertTrue(reports.hasBlockedAccounts());
    }
}
