package bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultAccountFactoryTest {
    private final AccountFactory factory = new DefaultAccountFactory();
    private final AccountNumber number = new AccountNumber("1111111111");

    @ParameterizedTest
    @MethodSource("accountTypes")
    void createsRequestedAccountWithGivenDetails(AccountType type, Class<? extends BankAccount> accountClass) {
        BankAccount account = factory.create(type, number, "Ivan", 1500);

        assertInstanceOf(accountClass, account);
        assertEquals(number, account.getNumber());
        assertEquals("Ivan", account.getOwner());
        assertEquals(1500, account.getBalance());
    }

    @Test
    void savingsAccountKeepsMinimumBalanceOfOneThousand() {
        BankAccount account = factory.create(AccountType.SAVINGS, number, "Ivan", 1500);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(501));
        account.withdraw(500);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void creditAccountHasLimitOfFiftyThousand() {
        BankAccount account = factory.create(AccountType.CREDIT, number, "Ivan", 0);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(50_001));
        account.withdraw(50_000);
        assertEquals(-50_000, account.getBalance());
    }

    private static Stream<Arguments> accountTypes() {
        return Stream.of(
            Arguments.of(AccountType.DEBIT, DebitAccount.class),
            Arguments.of(AccountType.SAVINGS, SavingsAccount.class),
            Arguments.of(AccountType.CREDIT, CreditAccount.class)
        );
    }
}
