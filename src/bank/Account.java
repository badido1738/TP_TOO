package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public abstract class Account {

    protected final String accountNumber;
    protected double balance;
    protected final List<Transaction> transactions = new ArrayList<>();

    protected Account(String accountNumber, double initial) {
        this.accountNumber = accountNumber;
        this.balance = initial;
    }

    public void deposit(double amount) {
        if(amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
        balance += amount;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        transactions.add(new Transaction(now, TransactionType.DEPOSIT, amount, balance));
    }

    public abstract void withdraw(double amount);

    public final String getAccountNumber() {
        return accountNumber;
    }

    public final double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
