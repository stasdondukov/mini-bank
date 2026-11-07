package bank;

public final class VipCommissionPolicy implements CommissionPolicy {
    @Override
    public double calculate(double amount) {
        return amount * 0.002;
    }
}
