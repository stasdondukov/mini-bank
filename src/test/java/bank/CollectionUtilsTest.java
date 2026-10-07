package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilsTest {

    @Test
    void testCopyDebitToBankAccount() {
        List<DebitAccount> source = List.of(
            new DebitAccount("1111111111", "Ivan", 100),
            new DebitAccount("2222222222", "Olga", 200)
        );
        List<BankAccount> target = new ArrayList<>();
        
        CollectionUtils.copy(source, target);
        
        assertEquals(2, target.size());
        assertEquals("1111111111", target.get(0).getNumber().value());
        assertEquals("2222222222", target.get(1).getNumber().value());
    }

    @Test
    void testCopyDebitToObject() {
        List<DebitAccount> source = List.of(
            new DebitAccount("1111111111", "Ivan", 100),
            new DebitAccount("2222222222", "Olga", 200)
        );
        List<Object> target = new ArrayList<>();
        
        CollectionUtils.copy(source, target);
        
        assertEquals(2, target.size());
        assertEquals("Ivan", ((DebitAccount) target.get(0)).getOwner());
    }

    @Test
    void testCopyBankToBankAccount() {
        List<BankAccount> source = List.of(
            new DebitAccount("1111111111", "Ivan", 100),
            new SavingsAccount("3333333333", "Petr", 300, 10)
        );
        List<BankAccount> target = new ArrayList<>();
        
        CollectionUtils.copy(source, target);
        
        assertEquals(2, target.size());
        assertEquals("1111111111", target.get(0).getNumber().value());
        assertEquals("3333333333", target.get(1).getNumber().value());
    }
}
