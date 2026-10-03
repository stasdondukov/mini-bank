package bank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankAccountEqualityTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount a = new DebitAccount("001", "Ivan", 1000.0);
        BankAccount b = new DebitAccount("001", "Ivan", 5000.0);
        assertEquals(a, b);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount a = new DebitAccount("001", "Ivan", 1000.0);
        BankAccount b = new DebitAccount("002", "Ivan", 1000.0);
        assertNotEquals(a, b);
    }

    @Test
    void accountEqualsItself() {
        BankAccount a = new DebitAccount("001", "Ivan", 1000.0);
        assertEquals(a, a);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount a = new DebitAccount("001", "Ivan", 1000.0);
        assertNotEquals(null, a);
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount a = new DebitAccount("001", "Ivan", 1000.0);
        BankAccount b = new DebitAccount("001", "Ivan", 5000.0);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void debitAndSavingsWithSameNumberAreEqual() {
        BankAccount debit = new DebitAccount("001", "Ivan", 1000.0);
        BankAccount savings = new SavingsAccount("001", "Ivan", 1000.0, 100.0);
        assertEquals(debit, savings);
    }

    @Test
    void debitAndCreditWithSameNumberAreEqual() {
        BankAccount debit = new DebitAccount("001", "Ivan", 1000.0);
        BankAccount credit = new CreditAccount("001", "Ivan", 1000.0, 5000.0);
        assertEquals(debit, credit);
    }

    @Test
    void savingsAndCreditWithSameNumberAreEqual() {
        BankAccount savings = new SavingsAccount("001", "Ivan", 1000.0, 100.0);
        BankAccount credit = new CreditAccount("001", "Ivan", 1000.0, 5000.0);
        assertEquals(savings, credit);
    }
}
