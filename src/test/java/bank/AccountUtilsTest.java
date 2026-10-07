package bank;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountUtilsTest {

    @Test
    void testTotalBalanceWithDebitAccounts() {
        List<DebitAccount> debitAccounts = List.of(
            new DebitAccount("1234567890", "Ivan", 100.0),
            new DebitAccount("0987654321", "Olga", 250.0)
        );
        
        assertEquals(350.0, AccountUtils.totalBalance(debitAccounts));
    }

    @Test
    void testTotalBalanceWithMixedAccounts() {
        List<BankAccount> mixedAccounts = List.of(
            new DebitAccount("1111111111", "Ivan", 100.0),
            new SavingsAccount("2222222222", "Olga", 500.0, 50.0)
        );
        
        assertEquals(600.0, AccountUtils.totalBalance(mixedAccounts));
    }

    @Test
    void testAddDemoDebitAccountsToDebitList() {
        java.util.List<DebitAccount> list = new java.util.ArrayList<>();
        AccountUtils.addDemoDebitAccounts(list);
        assertEquals(2, list.size());
    }

    @Test
    void testAddDemoDebitAccountsToBankList() {
        java.util.List<BankAccount> list = new java.util.ArrayList<>();
        list.add(new SavingsAccount("0000000000", "Existing", 100.0, 10.0));
        AccountUtils.addDemoDebitAccounts(list);
        assertEquals(3, list.size());
    }

    @Test
    void testAddDemoDebitAccountsToObjectList() {
        java.util.List<Object> list = new java.util.ArrayList<>();
        list.add("String Element");
        AccountUtils.addDemoDebitAccounts(list);
        assertEquals(3, list.size());
    }
}
