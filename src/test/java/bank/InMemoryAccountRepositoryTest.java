package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryAccountRepositoryTest {

    @Test
    void storesAccountsByNumberAndReturnsIndependentListings() {
        AccountRepository repository = new InMemoryAccountRepository();
        AccountNumber number = new AccountNumber("1111111111");
        BankAccount original = new DebitAccount(number, "Ivan", 1000);
        BankAccount replacement = new DebitAccount("1111111111", "Olga", 2000);

        assertTrue(repository.findById(number).isEmpty());
        repository.save(original);
        assertSame(original, repository.findById(number).orElseThrow());
        repository.save(replacement);
        assertSame(replacement, repository.findById(number).orElseThrow());

        List<BankAccount> listing = repository.findAll();
        assertEquals(1, listing.size());
        assertSame(replacement, listing.get(0));
        listing.clear();
        assertSame(replacement, repository.findById(number).orElseThrow());

        assertTrue(repository.deleteById(number));
        assertFalse(repository.deleteById(number));
        assertTrue(repository.findById(number).isEmpty());
    }
}
