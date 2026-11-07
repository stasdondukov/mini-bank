package bank;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public final class WeekendCommissionPolicy implements CommissionPolicy {
    private final Clock clock;
    private final CommissionPolicy weekdayPolicy;
    private final CommissionPolicy weekendPolicy;

    public WeekendCommissionPolicy(Clock clock, CommissionPolicy weekdayPolicy, CommissionPolicy weekendPolicy) {
        this.clock = Objects.requireNonNull(clock);
        this.weekdayPolicy = Objects.requireNonNull(weekdayPolicy);
        this.weekendPolicy = Objects.requireNonNull(weekendPolicy);
    }

    @Override
    public double calculate(double amount) {
        DayOfWeek day = LocalDate.now(clock).getDayOfWeek();
        boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
        CommissionPolicy policy = weekend ? weekendPolicy : weekdayPolicy;
        return policy.calculate(amount);
    }
}
