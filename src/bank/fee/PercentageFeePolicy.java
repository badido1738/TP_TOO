package bank.fee;

public class PercentageFeePolicy implements FeePolicy {
    private final double rate; // Ex: 0.01 pour 1%

    public PercentageFeePolicy(double rate) {
        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException("Rate must be between 0 and 1");
        }
        this.rate = rate;
    }

    @Override
    public double computeFee(double amount) {
        return amount * rate;
    }
}