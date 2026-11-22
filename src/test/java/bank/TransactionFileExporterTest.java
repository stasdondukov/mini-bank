package bank;

import bank.adapters.file.TransactionFileExporter;
import bank.domain.AccountNumber;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;
import bank.domain.TransactionType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionFileExporterTest {
    private final TransactionFileExporter exporter = new TransactionFileExporter();

    @Test
    void createsDirectoriesAndWritesTransactionRowsInOrder(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("exports/daily/transactions.txt");
        AccountNumber from = new AccountNumber("1111111111");
        AccountNumber to = new AccountNumber("0000000002");
        List<Transaction> transactions = List.of(
            new Transaction(1L, TransactionType.TRANSFER, from, to, 1000, TransactionStatus.SUCCESS),
            new Transaction(2L, TransactionType.TRANSFER, from, 150.5, TransactionStatus.REJECTED)
        );

        exporter.export(path, transactions);

        assertTrue(Files.isRegularFile(path));
        assertEquals(List.of(
            "id;from;to;amount;status",
            "1;1111111111;0000000002;1000.0;SUCCESS",
            "2;1111111111;;150.5;REJECTED"
        ), Files.readAllLines(path));
    }

    @Test
    void exportsAnEmptyListToARelativePathWithoutAParent() throws IOException {
        Path path = Files.createTempFile(Path.of("."), "mini-bank-export-", ".txt").getFileName();
        try {
            Files.writeString(path, "prior content");
            exporter.export(path, List.of());

            assertEquals(List.of("id;from;to;amount;status"), Files.readAllLines(path));
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
