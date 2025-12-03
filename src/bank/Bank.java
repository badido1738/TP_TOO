package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import bank.tx.Transaction;
import bank.tx.TransactionType;

public class Bank {

    public void transfer(Account from, Account to, double amount) {
        if(from == null || to == null) {
            throw new BusinessRuleViolation("Accounts cannot be null");
        }
        if(from == to) {
            throw new BusinessRuleViolation("Cannot transfer to the same account");
        }
        if(amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
        if(from.getBalance() < amount && !(from instanceof CreditAccount || from instanceof BusinessAccount)) {
            throw new BusinessRuleViolation("Insufficient funds for transfer");
        }

        try {
            from.withdraw(amount);
        } catch(RuntimeException e) {
            throw new BusinessRuleViolation("Withdraw failed: " + e.getMessage());
        }

        try {
            to.deposit(amount);
        } catch(RuntimeException e) {
            from.deposit(amount);
            throw new BusinessRuleViolation("Deposit failed: rollback done");
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        from.transactions.add(new Transaction(now, TransactionType.TRANSFER_OUT, amount, from.getBalance()));
        to.transactions.add(new Transaction(now, TransactionType.TRANSFER_IN, amount, to.getBalance()));
    }
}
