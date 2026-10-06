package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

    record DummyEntity(Integer id, String data) implements Identifiable<Integer> {
        @Override
        public Integer getId() {
            return id;
        }
    }

    private InMemoryRepository<Integer, DummyEntity> repository;

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
        repository.save(new DummyEntity(1, "A"));
        repository.save(new DummyEntity(1, "B"));

        assertEquals(1, repository.size());
        assertEquals("B", repository.findById(1).data());
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

        assertEquals(entity, repository.findById(1));
    }

    @Test
    void findByIdThrowsExceptionIfNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.findById(99));
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
}
