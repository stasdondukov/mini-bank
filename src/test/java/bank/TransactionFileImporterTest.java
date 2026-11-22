package bank;

import bank.adapters.dto.TransactionDto;
import bank.adapters.file.TransactionFileExporter;
import bank.adapters.file.TransactionFileImporter;
import bank.domain.AccountNumber;
import bank.domain.Transaction;
import bank.domain.TransactionStatus;
import bank.domain.TransactionType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionFileImporterTest {
    private final TransactionFileImporter importer = new TransactionFileImporter();

    @TempDir
    Path tempDir;

    @Test
    void countsOnlyRowsWhoseStatusIsExactlySuccess() throws IOException {
        Path path = tempDir.resolve("statistics.txt");
        Files.write(path, List.of(
            "id;from;to;amount;status",
            "1;1111111111;2222222222;1000.0;SUCCESS",
            "2;1111111111;;150.5;SUCCESS",
            "3;1111111111;2222222222;200.0;REJECTED",
            "4;1111111111;2222222222;500.0;NOT_SUCCESS",
            "5;SUCCESS;2222222222;100.0;REJECTED"
        ));

        assertEquals(2L, importer.countSuccessful(path));
    }

    @Test
    void reportsReadErrorsAsIOException() throws IOException {
        Path path = tempDir.resolve("invalid-encoding.txt");
        Files.write(path, new byte[] {(byte) 0xFF});

        assertThrows(IOException.class, () -> importer.countSuccessful(path));
    }

    @Test
    void loadsExporterOutputInOrderWithLeadingZeroesAndMissingAccounts() throws IOException {
        Path path = tempDir.resolve("transactions.txt");
        AccountNumber from = new AccountNumber("0000000001");
        AccountNumber to = new AccountNumber("0000000002");
        new TransactionFileExporter().export(path, List.of(
            new Transaction(1L, TransactionType.TRANSFER, from, to, 1000.5, TransactionStatus.SUCCESS),
            new Transaction(2L, TransactionType.TRANSFER, from, 150, TransactionStatus.REJECTED),
            new Transaction(3L, TransactionType.DEPOSIT, to, 200, TransactionStatus.SUCCESS)
        ));

        assertEquals(List.of(
            new TransactionDto(1, "0000000001", "0000000002", 1000.5, "SUCCESS"),
            new TransactionDto(2, "0000000001", null, 150.0, "REJECTED"),
            new TransactionDto(3, null, "0000000002", 200.0, "SUCCESS")
        ), importer.load(path));
    }

    @Test
    void headerOnlyReturnsAnEmptyList() throws IOException {
        Path path = tempDir.resolve("empty.txt");
        Files.write(path, List.of("id;from;to;amount;status"));

        assertTrue(importer.load(path).isEmpty());
    }

    @Test
    void rejectsAnIncorrectHeader() throws IOException {
        Path path = tempDir.resolve("wrong-header.txt");
        Files.write(path, List.of("1;1111111111;2222222222;1000.0;SUCCESS"));

        IOException error = assertThrows(IOException.class, () -> importer.load(path));
        assertTrue(error.getMessage().contains("line 1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2;1111111111;2222222222;1000.0",
        "2;1111111111;2222222222;amount;SUCCESS",
        "2;1111111111;2222222222;1000.0;UNKNOWN",
        "id;1111111111;2222222222;1000.0;SUCCESS"
    })
    void rejectsAnInvalidRowAndReportsItsLineNumber(String row) throws IOException {
        Path path = tempDir.resolve("invalid.txt");
        Files.write(path, List.of(
            "id;from;to;amount;status",
            "1;1111111111;2222222222;1000.0;SUCCESS",
            row
        ));

        IOException error = assertThrows(IOException.class, () -> importer.load(path));
        assertTrue(error.getMessage().contains("line 3"));
    }
}
