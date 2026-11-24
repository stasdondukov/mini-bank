package bank;

import bank.adapters.dto.AccountDto;
import bank.adapters.file.JsonStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonStorageTest {
    @Test
    void roundTripsAccountsOfDifferentTypesInOrder(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("accounts.json");
        List<AccountDto> source = List.of(
            new AccountDto("0000000001", "Иван", 1500.5, "DebitAccount"),
            new AccountDto("0000000002", "Ольга", -250.0, "CreditAccount"),
            new AccountDto("0000000003", "Анна", 3000.0, "SavingsAccount")
        );
        JsonStorage storage = new JsonStorage();
        storage.saveAccounts(path, source);

        List<AccountDto> restored = storage.loadAccounts(path);

        assertEquals(source, restored);
    }

    @Test
    void roundTripsAnEmptyList(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("empty.json");
        List<AccountDto> source = List.of();
        JsonStorage storage = new JsonStorage();
        storage.saveAccounts(path, source);

        List<AccountDto> restored = storage.loadAccounts(path);

        assertEquals(source, restored);
    }

    @Test
    void rejectsInvalidJson(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("invalid.json");
        Files.writeString(path, "[{!}]");

        assertThrows(JsonProcessingException.class, () -> new JsonStorage().loadAccounts(path));
    }

    @Test
    void loadingAMissingFileThrowsIOException(@TempDir Path tempDir) {
        Path path = tempDir.resolve("missing.json");

        assertThrows(IOException.class, () -> new JsonStorage().loadAccounts(path));
    }

    @Test
    void savesAccountsAsFormattedJsonWithStringAccountNumbers(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("accounts.json");
        List<AccountDto> accounts = List.of(
            new AccountDto("0000000001", "Иван", 1500.5, "DebitAccount")
        );

        new JsonStorage().saveAccounts(path, accounts);

        String json = Files.readString(path);
        ObjectMapper reader = new ObjectMapper();
        assertEquals(reader.readTree("""
            [{"number":"0000000001","owner":"Иван","balance":1500.5,"type":"DebitAccount"}]
            """), reader.readTree(json));
        assertTrue(json.lines().count() > 1);
    }
}
