package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public final class SavingsAccount extends Account {

    private final double interestRate;

    public SavingsAccount(String id, double initial, double interestRate) {
        super(id, initial);
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        if(balance > 0) {
            double interest = balance * interestRate;
            balance += interest;
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            transactions.add(new Transaction(now, TransactionType.INTEREST, interest, balance));
        }
    }

    @Override
    protected void checkSpecificRules(double totalAmount) {
        if(balance - totalAmount < 0) {
            throw new BusinessRuleViolation("Your balance is not enough");
        }
    }

    @Override
    protected void applyWithdraw(double totalAmount) {
        balance -= totalAmount;
    }
}