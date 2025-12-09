package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import bank.fee.FeePolicy;
import bank.fee.NoFeePolicy;
import bank.tx.Transaction;
import bank.tx.TransactionType;

public abstract class Account {

    protected final String accountNumber;
    protected double balance;
    protected final List<Transaction> transactions = new ArrayList<>();
    private FeePolicy feePolicy;
    private static final Logger logger = new Logger();
    private final List<AccountObserver> observers = new ArrayList<>();

    protected Account(String accountNumber, double initial) {
        this.accountNumber = accountNumber;
        this.balance = initial;
        this.feePolicy = new NoFeePolicy();
    }

    public void deposit(double amount) {
        if(amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
        balance += amount;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Transaction tx = new Transaction(now, TransactionType.DEPOSIT, amount, balance);
        transactions.add(tx);
        notifyObservers(tx);
    }

    public final void withdraw(double amount) {
        
        double totalAmount = calculateTotalWithdrawalAmount(amount);

        try {
            checkAmount(amount);
            checkSpecificRules(totalAmount); 
            applyWithdraw(totalAmount);      
            
            Transaction lastWithdrawalTx = recordWithdrawalTransaction(amount); 
            notifyObservers(lastWithdrawalTx); 
            
            Transaction lastFeeTx = applyFee(amount); 
            if (lastFeeTx != null) {
                notifyObservers(lastFeeTx); 
            }
            
            logger.logInfo("Withdraw OK: " + amount + " EUR from " + accountNumber);
        } catch (BusinessRuleViolation e) {
             logger.logError("Withdraw FAILED: " + amount + " EUR from " + accountNumber + " - " + e.getMessage());
             throw e; 
        }
    }

    private void checkAmount(double amount) {
        if (amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
    }

    protected double calculateTotalWithdrawalAmount(double requestedAmount) {
        return requestedAmount + feePolicy.computeFee(requestedAmount);
    }

    private Transaction recordWithdrawalTransaction(double requestedAmount) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Transaction tx = new Transaction(now, TransactionType.WITHDRAW, requestedAmount, balance);
        transactions.add(tx);
        return tx;
    }
    
    private Transaction applyFee(double requestedAmount) {
        double fee = feePolicy.computeFee(requestedAmount);
        if (fee > 0) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Transaction tx = new Transaction(now, TransactionType.FEE, fee, balance);
            transactions.add(tx);
            return tx;
        }
        return null;
    }
    
    protected abstract void checkSpecificRules(double totalAmount);

    protected abstract void applyWithdraw(double totalAmount);

    public final String getAccountNumber() {
        return accountNumber;
    }

    public final double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setFeePolicy(FeePolicy feePolicy) {
        this.feePolicy = feePolicy;
    }
    
    public void addObserver(AccountObserver obs) {
        observers.add(obs);
    }
    
    private void notifyObservers(Transaction tx) {
        for (var obs : observers) {
            obs.onTransaction(this, tx);
        }
    }
}