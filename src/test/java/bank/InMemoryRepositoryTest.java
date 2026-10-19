package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

    record DummyEntity(Integer id, String data) implements Identifiable<Integer> {
        @Override
        public Integer getId() {
            return id;
        }
    }

    private Repository<Integer, DummyEntity> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
    }

    @Test
    void saveThrowsExceptionOnNull() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void saveReplacesEntityWithSameId() {
        DummyEntity oldEntity = new DummyEntity(1, "A");
        DummyEntity newEntity = new DummyEntity(1, "B");
        repository.save(oldEntity);
        repository.save(newEntity);

        assertEquals(1, repository.size());
        assertSame(newEntity, repository.findById(1));
    }

    @Test
    void saveAddsNewEntity() {
        repository.save(new DummyEntity(1, "A"));
        repository.save(new DummyEntity(2, "B"));

        assertEquals(2, repository.size());
    }

    @Test
    void findByIdReturnsEntity() {
        DummyEntity entity = new DummyEntity(1, "A");
        repository.save(entity);

        assertSame(entity, repository.findById(1));
    }

    @Test
    void findByIdReturnsNullIfNotFound() {
        assertNull(repository.findById(99));
    }

    @Test
    void existsByIdReturnsTrueIfFound() {
        repository.save(new DummyEntity(1, "A"));
        assertTrue(repository.existsById(1));
    }

    @Test
    void existsByIdReturnsFalseIfNotFound() {
        assertFalse(repository.existsById(99));
    }

    @Test
    void sizeReturnsCorrectCount() {
        assertEquals(0, repository.size());
        repository.save(new DummyEntity(1, "A"));
        assertEquals(1, repository.size());
        repository.save(new DummyEntity(2, "B"));
        assertEquals(2, repository.size());
    }

    @Test
    void deleteByIdRemovesExistingEntity() {
        DummyEntity first = new DummyEntity(1, "A");
        DummyEntity second = new DummyEntity(2, "B");
        repository.save(first);
        repository.save(second);

        assertTrue(repository.deleteById(1));
        assertNull(repository.findById(1));
        assertFalse(repository.existsById(1));
        assertSame(second, repository.findById(2));
        assertEquals(1, repository.size());
    }

    @Test
    void deleteByIdReturnsFalseIfNotFound() {
        DummyEntity entity = new DummyEntity(1, "A");
        repository.save(entity);

        assertFalse(repository.deleteById(99));
        assertSame(entity, repository.findById(1));
        assertEquals(1, repository.size());
    }

    @Test
    void findAllReturnsAllEntities() {
        DummyEntity first = new DummyEntity(1, "A");
        DummyEntity second = new DummyEntity(2, "B");
        repository.save(first);
        repository.save(second);

        List<DummyEntity> entities = repository.findAll();

        assertEquals(2, entities.size());
        assertTrue(entities.contains(first));
        assertTrue(entities.contains(second));
    }

    @Test
    void findAllReturnsEmptyListForEmptyRepository() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAllReturnsIndependentList() {
        DummyEntity first = new DummyEntity(1, "A");
        DummyEntity second = new DummyEntity(2, "B");
        repository.save(first);
        List<DummyEntity> entities = repository.findAll();

        repository.save(second);
        assertEquals(1, entities.size());

        entities.clear();
        assertEquals(2, repository.size());
        assertSame(first, repository.findById(1));
        assertSame(second, repository.findById(2));
    }

    @Test
    void savingSameIdReplacesOldValue() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
        AccountNumber id = new AccountNumber("0000000001");
        BankAccount oldAccount = new DebitAccount(id, "Ivan", 1000);
        BankAccount newAccount = new DebitAccount(id, "Petr", 5000);
        accounts.save(oldAccount);
        accounts.save(newAccount);

        assertEquals(1, accounts.size());
        assertSame(newAccount, accounts.findById(new AccountNumber("0000000001")));
    }
}
