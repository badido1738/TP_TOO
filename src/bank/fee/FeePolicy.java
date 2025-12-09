package bank.fee;

public interface FeePolicy {

    double computeFee(double amount);
}