package bank.domain.service;

public class NoFeePolicy implements FeePolicy {
    @Override
    public double computeFee(double amount) {
        return 0.0;
    }
}