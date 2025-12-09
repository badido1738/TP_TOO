package bank.fee;

public class FixedFeePolicy implements FeePolicy {
    private final double fee;

    public FixedFeePolicy(double fee) {
        if (fee < 0) {
            throw new IllegalArgumentException("Fixed fee cannot be negative");
        }
        this.fee = fee;
    }

    @Override
    public double computeFee(double amount) {
        return fee;
    }
}