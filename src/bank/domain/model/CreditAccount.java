package bank.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.domain.exceptions.BusinessRuleViolation;

public class CreditAccount extends Account {

    private final double creditLimit;

    public CreditAccount(String id, double initial, double creditLimit) {
        super(id, initial);
        this.creditLimit = creditLimit;
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
    
    public double getCreditLimit() {
        return creditLimit;
    }
}