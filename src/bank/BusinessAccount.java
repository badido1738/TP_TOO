package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.tx.Transaction;
import bank.tx.TransactionType;

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
    public void withdraw(double amount) {
        if(amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
        if(balance - amount < -creditLimit) {
            throw new BusinessRuleViolation("Withdrawal exceeds credit limit");
        }
        balance -= amount;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        transactions.add(new Transaction(now, TransactionType.WITHDRAW, amount, balance));
    }

    @Override
    public void deposit(double amount) {
        if(amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
        balance += amount;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        transactions.add(new Transaction(now, TransactionType.DEPOSIT, amount, balance));
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}
