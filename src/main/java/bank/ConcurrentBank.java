package bank;

import bank.application.AccountSummary;
import bank.application.BankReportService;
import bank.application.TransferApplicationService;
import bank.domain.AccountNumber;

import java.util.List;

final class ConcurrentBank {
    private final TransferApplicationService transfers;
    private final BankReportService reports;
    private final boolean synchronizedTransfers;

    ConcurrentBank(TransferApplicationService transfers, BankReportService reports) {
        this(transfers, reports, true);
    }

    ConcurrentBank(TransferApplicationService transfers, BankReportService reports,
                   boolean synchronizedTransfers) {
        this.transfers = transfers;
        this.reports = reports;
        this.synchronizedTransfers = synchronizedTransfers;
    }

    void transfer(AccountNumber from, AccountNumber to, double amount) {
        if (synchronizedTransfers) {
            synchronized (this) {
                transfers.transfer(from, to, amount);
            }
        } else {
            transfers.transfer(from, to, amount);
        }
    }

    synchronized List<AccountSummary> snapshot() {
        return reports.getAccountSummaries();
    }

    synchronized double totalBalance() {
        return reports.totalBalance();
    }
}
