package bank;

import java.util.List;

public interface Repository<ID, T extends Identifiable<ID>> {
    void save(T value);
    
    T findById(ID id);
    
    boolean existsById(ID id);

    boolean deleteById(ID id);

    List<T> findAll();
    
    int size();
}
