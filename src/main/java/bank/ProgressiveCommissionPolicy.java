package bank;

public final class ProgressiveCommissionPolicy implements CommissionPolicy {
    private final double threshold;
    private final CommissionPolicy aboveThreshold;

    public ProgressiveCommissionPolicy(double threshold, double percent) {
        if (!Double.isFinite(threshold) || threshold < 0) {
            throw new IllegalArgumentException("Threshold must be finite and non-negative");
        }
        if (!Double.isFinite(percent) || percent < 0) {
            throw new IllegalArgumentException("Percent must be finite and non-negative");
        }
        this.threshold = threshold;
        this.aboveThreshold = new PercentCommission(percent);
    }

    @Override
    public double calculate(double amount) {
        return amount > threshold ? aboveThreshold.calculate(amount) : 0.0;
    }
}
