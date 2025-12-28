package bank.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import bank.domain.exceptions.InvalidAmountException;
import bank.domain.exceptions.TransferException;
import bank.domain.exceptions.UnknownAccountException;

public class Bank {

    private final Map<String, Account> accounts;
    
    // SUPPRESSION : private final Logger logger = new Logger(); 
    // Le domaine ne doit pas connaître le Logger technique.

    public Bank(Map<String, Account> accounts) {
        this.accounts = accounts;
    }

    public void transfer(String fromId, String toId, double amount) {
        if (!accounts.containsKey(fromId)) {
            throw new UnknownAccountException("Source account not found: " + fromId);
        }
        if (!accounts.containsKey(toId)) {
            throw new UnknownAccountException("Destination account not found: " + toId);
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be > 0");
        }
        if (fromId.equals(toId)) {
            throw new InvalidAmountException("Cannot transfer to the same account");
        }

        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        // Sauvegarde de l'état avant opération (pour rollback)
        double initialFrom = from.getBalance();
        double initialTo = to.getBalance();

        try {
            // 1. Exécution des mouvements
            // Note : withdraw/deposit déclenchent déjà les Observer (AuditService)
            from.withdraw(amount);
            to.deposit(amount);

            // 2. Ajout des transactions spécifiques "TRANSFER"
            // (Possible car Bank et Account sont dans le même package domain.model)
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            from.transactions.add(new Transaction(now, TransactionType.TRANSFER_OUT, amount, from.getBalance()));
            to.transactions.add(new Transaction(now, TransactionType.TRANSFER_IN, amount, to.getBalance()));

            // SUPPRESSION : logger.logInfo(...) 
            // C'est le rôle du BankService (Application) de logger le succès global si besoin.

        } catch (Exception e) {
            // 3. Rollback manuel en cas d'erreur
            from.balance = initialFrom;
            to.balance = initialTo;

            // SUPPRESSION : logger.logError(...)
            
            // On relance l'exception pour que la couche Application sache qu'il y a eu un échec
            throw new TransferException("Echec du transfert", e);
        }
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }
}