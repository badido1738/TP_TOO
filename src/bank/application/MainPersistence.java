package bank.application;

import bank.domain.exceptions.PersistenceException;
import bank.domain.model.Account;
import bank.domain.model.Bank;
import bank.domain.model.CreditAccount;
import bank.domain.model.SavingsAccount;
import bank.domain.service.BankRepository;
import bank.infrastructure.FileBankRepository;
import bank.infrastructure.TextBankSerializer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MainPersistence {

    public static void main(String[] args) {
        // 1. Préparation des données (Comptes et Transactions)
        SavingsAccount s1 = new SavingsAccount("SA-1", 1000.0, 0.02);
        CreditAccount c1 = new CreditAccount("CR-1", -50.0, 500.0);

        Map<String, Account> accounts = new HashMap<>();
        accounts.put(s1.getAccountNumber(), s1);
        accounts.put(c1.getAccountNumber(), c1);

        Bank bank = new Bank(accounts);

        // Ajout de quelques transactions
        try {
            s1.withdraw(100.0); // Solde devient 900
            c1.deposit(50.0);   // Solde devient 0
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("--- État initial de la banque ---");
        System.out.println("Compte SA-1 solde : " + s1.getBalance());
        System.out.println("Compte CR-1 solde : " + c1.getBalance());

        // 2. Configuration de la persistance
        BankRepository repo = new FileBankRepository(
            Path.of("bank.txt"),
            new TextBankSerializer()
        );

        // 3. Sauvegarde
        try {
            repo.save(bank);
            System.out.println("\n[OK] Banque sauvegardée dans bank.txt");
        } catch (PersistenceException e) {
            System.err.println("Erreur de sauvegarde : " + e.getMessage());
        }

        // 4. Chargement pour vérification
        try {
            Bank loadedBank = repo.load();
            
            System.out.println("\n--- État après chargement ---");
            // Vérification basique (suppose que Bank expose getAccounts() comme demandé précédemment)
            Account loadedS1 = loadedBank.getAccounts().get("SA-1");
            Account loadedC1 = loadedBank.getAccounts().get("CR-1");

            System.out.println("Compte SA-1 solde : " + loadedS1.getBalance());
            System.out.println("Compte CR-1 solde : " + loadedC1.getBalance());
            
            // Vérification simple de l'historique
            System.out.println("Transactions SA-1 : " + loadedS1.getTransactions().size()); 

            if (loadedS1.getBalance() == s1.getBalance()) {
                System.out.println("\nSUCCESS: Les soldes correspondent !");
            } else {
                System.out.println("\nFAIL: Les soldes ne correspondent pas.");
            }

        } catch (PersistenceException e) {
            System.err.println("Erreur de chargement : " + e.getMessage());
        }
    }
}