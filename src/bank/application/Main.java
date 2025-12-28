package bank.application;

import bank.domain.model.Account;
import bank.domain.model.CreditAccount;
import bank.domain.model.SavingsAccount;
import bank.infrastructure.AuditService;
import bank.infrastructure.PostgresBankRepository;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== DÉMARRAGE DE LA DÉMO UPPA BANK (PostgreSQL) ===");

        // initialisation avec le Repository PostgreSQL
        var repository = new PostgresBankRepository();
        var auditService = new AuditService();
        BankService service = new BankService(repository);

        // ajout des comptes et observateurs
        SavingsAccount sa = new SavingsAccount("SA-123", 1000.0, 0.02);
        CreditAccount ca = new CreditAccount("CR-456", -50.0, 500.0);

        service.getBank().getAccounts().put(sa.getAccountNumber(), sa);
        service.getBank().getAccounts().put(ca.getAccountNumber(), ca);

        service.addObserver("SA-123", auditService);
        service.addObserver("CR-456", auditService);

        try {
            System.out.println(" -> Retrait de 100€ sur SA-123");
            sa.withdraw(100.0);

            System.out.println(" -> Virement de 200€ : SA-123 vers CR-456");
            service.transfer("SA-123", "CR-456", 200.0);
        } catch (Exception e) {
            System.err.println("ERREUR : " + e.getMessage());
        }

        service.save();
        System.out.println(" -> Données sauvegardées en BDD.");

        //rechargement depuis la base pour vérification
        BankService reloadedService = new BankService(new PostgresBankRepository());

        Account loadedSA = reloadedService.getBank().getAccounts().get("SA-123");
        Account loadedCA = reloadedService.getBank().getAccounts().get("CR-456");

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