package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public final class CreditAccount extends Account {

    private final double creditLimit;

    public CreditAccount(String id, double initial, double creditLimit) {
        super(id, initial);
        this.creditLimit = creditLimit;
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
        String now = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        transactions.add(new Transaction(now, TransactionType.WITHDRAW, amount, balance));
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}
