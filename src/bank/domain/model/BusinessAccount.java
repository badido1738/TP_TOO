package bank.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.domain.exceptions.BusinessRuleViolation;

public final class BusinessAccount extends Account {

    private final double interestRate;
    private final double creditLimit;

    public BusinessAccount(String id, double initial, double interestRate, double creditLimit) {
        super(id, initial);
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
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
        if(balance - totalAmount < -creditLimit) {
            throw new BusinessRuleViolation("Withdrawal exceeds credit limit");
        }
    }

    @Override
    protected void applyWithdraw(double totalAmount) {
        balance -= totalAmount;
    }
    
    public double getInterestRate() {
        return interestRate;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}