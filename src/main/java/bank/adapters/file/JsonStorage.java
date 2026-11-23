package bank.adapters.file;

import bank.adapters.dto.AccountDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class JsonStorage {
    private final ObjectMapper mapper = new ObjectMapper();

    public void saveAccounts(Path path, List<AccountDto> accounts) throws IOException {
        mapper.writerWithDefaultPrettyPrinter()
            .writeValue(path.toFile(), accounts);
    }

    public List<AccountDto> loadAccounts(Path path) throws IOException {
        return mapper.readValue(path.toFile(), new TypeReference<List<AccountDto>>() {});
    }
}
