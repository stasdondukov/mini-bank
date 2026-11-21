package bank.domain;

public class NoCommission implements CommissionPolicy {

    @Override
    public double calculate(double amount) {
        return 0;
    }
}
