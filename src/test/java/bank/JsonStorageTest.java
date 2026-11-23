package bank;

import bank.adapters.dto.AccountDto;
import bank.adapters.file.JsonStorage;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonStorageTest {
    @Test
    void loadsSavedAccountsInOrderAsDtos(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("accounts.json");
        List<AccountDto> accounts = List.of(
            new AccountDto("0000000001", "Иван", 1500.5, "DebitAccount"),
            new AccountDto("0000000002", "Ольга", -250.0, "CreditAccount")
        );
        JsonStorage storage = new JsonStorage();
        storage.saveAccounts(path, accounts);

        List<AccountDto> loaded = storage.loadAccounts(path);

        assertEquals(accounts, loaded);
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
