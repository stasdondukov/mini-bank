package bank;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRepository<ID, T extends Identifiable<ID>> implements Repository<ID, T> {

    private final List<T> values = new ArrayList<>();

    @Override
    public void save(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot save null entity");
        }

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getId().equals(value.getId())) {
                values.set(i, value);
                return;
            }
        }
        
        values.add(value);
    }

    @Override
    public T findById(ID id) {
        for (T value : values) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        throw new EntityNotFoundException("Entity with ID " + id + " not found");
    }

    @Override
    public boolean existsById(ID id) {
        for (T value : values) {
            if (value.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return values.size();
    }
}
