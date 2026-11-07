package bank;

public final class FixedCommissionPolicy implements CommissionPolicy {
    private final double fee;

    public FixedCommissionPolicy(double fee) {
        if (!Double.isFinite(fee) || fee < 0) {
            throw new IllegalArgumentException("Fee must be finite and non-negative");
        }
        this.fee = fee;
    }

    @Override
    public double calculate(double amount) {
        return fee;
    }
}
