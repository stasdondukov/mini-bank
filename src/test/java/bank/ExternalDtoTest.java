package bank;

import bank.adapters.dto.AccountDto;
import bank.adapters.dto.TransactionDto;
import bank.application.BlockedAccountRegistry;
import bank.application.TransactionJournal;
import bank.application.TransferEventPublisher;
import bank.application.TransferService;
import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.CreditAccount;
import bank.domain.DebitAccount;
import bank.domain.NoCommission;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;
import bank.domain.TransactionType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalDtoTest {
    @Test
    void accountDtoKeepsSnapshotAndPreservesLeadingZeroes() {
        BankAccount account = new CreditAccount("0000000001", "Ivan", 1500, 500);
        AccountDto dto = AccountDto.fromDomain(account);

        account.deposit(100);

        assertEquals(new AccountDto("0000000001", "Ivan", 1500, "CreditAccount"), dto);
    }

    @Test
    void transferJournalProvidesBothAccountNumbers() {
        BankAccount from = new DebitAccount("0000000001", "Ivan", 1500);
        BankAccount to = new DebitAccount("0000000002", "Olga", 500);
        TransactionJournal journal = new TransactionJournal();
        TransferService service = new TransferService(
            new NoCommission(), new TransferEventPublisher(), new BlockedAccountRegistry(), journal
        );

        service.transfer(from, to, 250);

        TransactionDto dto = TransactionDto.fromDomain(journal.findByAccount(from.getId()).get(0));
        assertEquals(new TransactionDto(1, "0000000001", "0000000002", 250, "SUCCESS"), dto);
    }

    @Test
    void olderTransferKeepsUnknownDestinationAbsent() {
        AccountNumber number = new AccountNumber("0000000001");
        Transaction transaction = new Transaction(7L, TransactionType.TRANSFER, number, 250, TransactionStatus.REJECTED);

        assertEquals(new TransactionDto(7, "0000000001", null, 250, "REJECTED"),
            TransactionDto.fromDomain(transaction));
    }

    @Test
    void depositAndWithdrawalExposeOnlyTheKnownSide() {
        AccountNumber number = new AccountNumber("0000000001");
        Transaction deposit = new Transaction(1L, TransactionType.DEPOSIT, number, 250, TransactionStatus.SUCCESS);
        Transaction withdrawal = new Transaction(2L, TransactionType.WITHDRAWAL, number, 150, TransactionStatus.SUCCESS);

        assertEquals(new TransactionDto(1, null, "0000000001", 250, "SUCCESS"),
            TransactionDto.fromDomain(deposit));
        assertEquals(new TransactionDto(2, "0000000001", null, 150, "SUCCESS"),
            TransactionDto.fromDomain(withdrawal));
    }
}
