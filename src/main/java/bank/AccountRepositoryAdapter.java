package bank;

import java.util.List;
import java.util.Optional;

public final class AccountRepositoryAdapter implements AccountRepository {
    private final Repository<AccountNumber, BankAccount> repository;

    public AccountRepositoryAdapter(Repository<AccountNumber, BankAccount> repository) {
        this.repository = repository;
    }

    @Override
    public Optional<BankAccount> findById(AccountNumber number) {
        return Optional.ofNullable(repository.findById(number));
    }

    @Override
    public void save(BankAccount account) {
        repository.save(account);
    }

    @Override
    public boolean deleteById(AccountNumber number) {
        return repository.deleteById(number);
    }

    @Override
    public List<BankAccount> findAll() {
        return repository.findAll();
    }
}
