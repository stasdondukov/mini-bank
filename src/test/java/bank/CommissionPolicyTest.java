package bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommissionPolicyTest {

    @Test
    void fixedFeeIsIndependentOfAmount() {
        CommissionPolicy policy = new FixedCommissionPolicy(12.5);

        assertEquals(12.5, policy.calculate(100));
        assertEquals(12.5, policy.calculate(10000));
    }

    @Test
    void progressiveFeeAppliesToWholeAmountOnlyAboveThreshold() {
        CommissionPolicy policy = new ProgressiveCommissionPolicy(100_000, 1.5);

        assertEquals(0.0, policy.calculate(99_999));
        assertEquals(0.0, policy.calculate(100_000));
        assertEquals(1650.0, policy.calculate(110_000));
    }

    @Test
    void weekendPolicyUsesClockZoneAndBothWeekendDays() {
        CommissionPolicy weekday = new NoCommission();
        CommissionPolicy weekend = new FixedCommissionPolicy(25);
        Instant friday = Instant.parse("2025-01-03T23:30:00Z");

        assertEquals(0.0, new WeekendCommissionPolicy(
            Clock.fixed(friday, ZoneOffset.UTC), weekday, weekend
        ).calculate(1000));
        assertEquals(25.0, new WeekendCommissionPolicy(
            Clock.fixed(friday, ZoneOffset.ofHours(8)), weekday, weekend
        ).calculate(1000));
        assertEquals(25.0, new WeekendCommissionPolicy(
            Clock.fixed(Instant.parse("2025-01-05T12:00:00Z"), ZoneOffset.UTC), weekday, weekend
        ).calculate(1000));
    }

    @ParameterizedTest
    @MethodSource("transferPolicies")
    void transferUsesInjectedCommissionPolicy(CommissionPolicy policy, double expectedFee) {
        BankAccount from = new DebitAccount("1111111111", "Ivan", 20000);
        BankAccount to = new DebitAccount("2222222222", "Olga", 0);
        TransferService service = new TransferService(
            policy, message -> {}, new BlockedAccountRegistry(), new TransactionJournal()
        );

        service.transfer(from, to, 10000);

        assertEquals(10000 - expectedFee, from.getBalance());
        assertEquals(10000, to.getBalance());
    }

    private static Stream<Arguments> transferPolicies() {
        Clock saturday = Clock.fixed(Instant.parse("2025-01-04T12:00:00Z"), ZoneOffset.UTC);
        return Stream.of(
            Arguments.of(new VipCommissionPolicy(), 20.0),
            Arguments.of(new FixedCommissionPolicy(12.5), 12.5),
            Arguments.of(new ProgressiveCommissionPolicy(5000, 1.5), 150.0),
            Arguments.of(new WeekendCommissionPolicy(
                saturday, new NoCommission(), new FixedCommissionPolicy(25)
            ), 25.0)
        );
    }
}
