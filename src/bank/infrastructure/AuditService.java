package bank.infrastructure;

import bank.domain.model.Account;
import bank.domain.model.Transaction;
import bank.domain.service.AccountObserver;

public class AuditService implements AccountObserver {

    @Override
    public void onTransaction(Account acc, Transaction tx) {
        String logMessage = String.format(
            "AUDIT LOG | Compte: %s | Type: %s | Montant: %.2f | Solde actuel: %.2f",
            acc.getAccountNumber(), 
            tx.getType(), 
            tx.getAmount(), 
            acc.getBalance()
        );
        // Simulation de l'enregistrement dans un fichier d'audit indépendant
        System.out.println(">>> " + logMessage);
    }
}