package bank;

import bank.application.AccountSummary;
import bank.application.BankReportService;
import bank.application.TransferApplicationService;
import bank.domain.AccountNumber;

import java.util.List;

final class ConcurrentBank {
    private final TransferApplicationService transfers;
    private final BankReportService reports;

    ConcurrentBank(TransferApplicationService transfers, BankReportService reports) {
        this.transfers = transfers;
        this.reports = reports;
    }

    synchronized void transfer(AccountNumber from, AccountNumber to, double amount) {
        transfers.transfer(from, to, amount);
    }

    synchronized List<AccountSummary> snapshot() {
        return reports.getAccountSummaries();
    }
}
