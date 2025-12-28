package bank.application;

import java.nio.file.Path;

import bank.domain.model.Account;
import bank.domain.model.CreditAccount;
import bank.domain.model.SavingsAccount;
import bank.infrastructure.AuditService;
import bank.infrastructure.FileBankRepository;
import bank.infrastructure.TextBankSerializer;

/**
 * Scénario de démonstration finale pour le TP8.
 * Orchestre le tout via le BankService.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== DÉMARRAGE DE LA DÉMO UPPA BANK ===");

        // 1. Mise en place de l'Infrastructure
        // On définit où stocker les données et comment les traduire en texte
        Path dbPath = Path.of("bank_demo.txt");
        var serializer = new TextBankSerializer();
        var repository = new FileBankRepository(dbPath, serializer);
        var auditService = new AuditService();

        // 2. Initialisation du Service Applicatif
        BankService service = new BankService(repository);
        
        // Pour la démo, on repart d'une banque propre (optionnel)
        service.getBank().getAccounts().clear();

        // 3. Création et Ajout de Comptes (Data)
        System.out.println("\n[1] Création des comptes...");
        SavingsAccount sa = new SavingsAccount("SA-123", 1000.0, 0.02); // Solde 1000
        CreditAccount ca = new CreditAccount("CR-456", -50.0, 500.0);   // Solde -50
        
        // On ajoute les comptes à la banque gérée par le service
        service.getBank().getAccounts().put(sa.getAccountNumber(), sa);
        service.getBank().getAccounts().put(ca.getAccountNumber(), ca);

        // On branche l'audit (Observer) pour voir les logs dans la console
        service.addObserver("SA-123", auditService);
        service.addObserver("CR-456", auditService);

        // 4. Exécution des opérations métier
        System.out.println("\n[2] Exécution des opérations...");
        try {
            // Retrait simple
            System.out.println(" -> Retrait de 100€ sur SA-123");
            sa.withdraw(100.0); // Reste 900

            // Virement via le service
            System.out.println(" -> Virement de 200€ : SA-123 vers CR-456");
            service.transfer("SA-123", "CR-456", 200.0); 
            // SA-123 : 900 - 200 = 700
            // CR-456 : -50 + 200 = 150

        } catch (Exception e) {
            System.err.println("ERREUR : " + e.getMessage());
        }

        // 5. Sauvegarde sur le disque
        System.out.println("\n[3] Sauvegarde des données...");
        service.save();
        System.out.println(" -> Données écrites dans " + dbPath.toAbsolutePath());

        // 6. Simulation d'un redémarrage (Rechargement)
        System.out.println("\n[4] Simulation redémarrage et vérification...");
        
        // On crée un NOUVEAU service avec un NOUVEAU repository pour prouver que ça vient du fichier
        BankService reloadedService = new BankService(new FileBankRepository(dbPath, serializer));
        
        Account loadedSA = reloadedService.getBank().getAccounts().get("SA-123");
        Account loadedCA = reloadedService.getBank().getAccounts().get("CR-456");

        // 7. Affichage de l'état final
        printAccountState(loadedSA);
        printAccountState(loadedCA);

        System.out.println("\n=== FIN DE LA DÉMO ===");
    }

    private static void printAccountState(Account acc) {
        if (acc == null) {
            System.out.println("Compte introuvable !");
            return;
        }
        System.out.println("------------------------------------------------");
        System.out.printf("Compte %s | Solde final : %.2f EUR%n", acc.getAccountNumber(), acc.getBalance());
        System.out.println("Historique (" + acc.getTransactions().size() + " opérations) :");
        acc.getTransactions().forEach(tx -> System.out.println("   * " + tx));
    }
}