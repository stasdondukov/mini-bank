package bank;

import bank.domain.AccountNumber;
import bank.domain.BankAccount;
import bank.domain.CreditAccount;
import bank.domain.DebitAccount;
import bank.domain.InsufficientFundsException;
import bank.domain.SavingsAccount;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccountContractTest {
    @ParameterizedTest
    @MethodSource("accounts")
    void withdrawalUsesTheSameContractForEveryAccount(BankAccount account, double availableAmount) {
        double initialBalance = account.getBalance();

        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-1));
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(availableAmount + 1));
        assertEquals(initialBalance, account.getBalance());

        account.withdraw(availableAmount);

        assertEquals(initialBalance - availableAmount, account.getBalance());
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1));
        assertEquals(initialBalance - availableAmount, account.getBalance());
    }

    private static Stream<Arguments> accounts() {
        AccountNumber number = new AccountNumber("1111111111");
        return Stream.of(
            Arguments.of(new DebitAccount(number, "Ivan", 1500), 1500.0),
            Arguments.of(new SavingsAccount(number, "Ivan", 1500, 1000), 500.0),
            Arguments.of(new CreditAccount(number, "Ivan", 1500, 500), 2000.0)
        );
    }
}
