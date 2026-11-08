package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryAccountRepository implements AccountRepository {
    private final Map<AccountNumber, BankAccount> data = new HashMap<>();

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        return Optional.ofNullable(data.get(number));
    }

    @Override
    public void save(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
        data.put(account.getId(), account);
    }

    @Override
    public boolean deleteById(AccountNumber number) {
        return data.remove(number) != null;
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(data.values());
    }
}
