package bank.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import bank.domain.exceptions.BusinessRuleViolation;
import bank.domain.service.AccountObserver;
import bank.domain.service.FeePolicy;
import bank.domain.service.NoFeePolicy;

/**
 * Classe abstraite représentant un compte bancaire.
 * Fait partie du Domaine Métier (Domain Model).
 * Ne dépend d'aucun outil technique (pas de Logger, pas de java.io).
 */
public abstract class Account {

    protected final String accountNumber;
    protected double balance;
    
    // Historique des transactions
    public final List<Transaction> transactions = new ArrayList<>();
    
    // Stratégie de frais (Strategy Pattern)
    private FeePolicy feePolicy;
    
    // Liste des observateurs pour l'audit (Observer Pattern)
    private final List<AccountObserver> observers = new ArrayList<>();

    protected Account(String accountNumber, double initial) {
        this.accountNumber = accountNumber;
        this.balance = initial;
        // Par défaut, pas de frais
        this.feePolicy = new NoFeePolicy();
    }

    /**
     * Effectue un dépôt sur le compte.
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new BusinessRuleViolation("Amount has to be > 0");
        }
        balance += amount;
        
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Transaction tx = new Transaction(now, TransactionType.DEPOSIT, amount, balance);
        
        transactions.add(tx);
        notifyObservers(tx); // Notifie l'AuditService
    }

    /**
     * Méthode modèle (Template Method) pour le retrait.
     * Gère l'orchestration : vérification -> règles spécifiques -> application -> frais.
     */
    public final void withdraw(double amount) {
        
        double totalAmount = calculateTotalWithdrawalAmount(amount);

        // 1. Vérifications (Validation)
        checkAmount(amount);
        checkSpecificRules(totalAmount); 
        
        // 2. Application du retrait (Modification d'état)
        applyWithdraw(totalAmount);      
        
        // 3. Enregistrement transaction principale
        Transaction lastWithdrawalTx = recordWithdrawalTransaction(amount); 
        notifyObservers(lastWithdrawalTx); // L'audit se fait ici via l'observer
        
        // 4. Application et enregistrement des frais éventuels
        Transaction lastFeeTx = applyFee(amount); 
        if (lastFeeTx != null) {
            notifyObservers(lastFeeTx); 
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
    
    /**
     * Méthode technique utilisée UNIQUEMENT par la couche de persistance
     * pour recharger l'historique depuis un fichier sans impacter le solde.
     */
    public void restoreTransaction(Transaction tx) {
        this.transactions.add(tx);
    }

    // --- Méthodes abstraites (Template Steps) ---

    protected abstract void checkSpecificRules(double totalAmount);

    protected abstract void applyWithdraw(double totalAmount);

    // --- Getters & Setters ---

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
    
    // --- Gestion des Observateurs ---
    
    public void addObserver(AccountObserver obs) {
        observers.add(obs);
    }
    
    private void notifyObservers(Transaction tx) {
        for (var obs : observers) {
            obs.onTransaction(this, tx);
        }
    }
}