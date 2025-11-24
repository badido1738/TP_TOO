package bank;
import bank.HelloBank;

public class Main {

    public static void main(String[] args) {

        SavingsAccount a1 = new SavingsAccount("SA-1001", 200.0, 0.018);
        Account a2 = new CreditAccount("CA-9001", 0.0, 500.0);

        Bank bank = new Bank(); 

        try {
            a1.deposit(50);
            a1.withdraw(20);
            a2.withdraw(100);

            a1.applyInterest();

            //transfer reussi
            try {
                System.out.println("\nTransfert réussi : 50€ de a1 → a2");
                bank.transfer(a1, a2, 50);
            } catch (BusinessRuleViolation e) {
                System.out.println("Erreur transfert réussi : " + e.getMessage());
            }

            //transfer echoue
            try {
                System.out.println("\nTransfert échoué : 10 000€ de a1 → a2");
                bank.transfer(a1, a2, 10_000);
            } catch (BusinessRuleViolation e) {
                System.out.println("Erreur attendue (transfert impossible) : " + e.getMessage());
            }

            System.out.printf("[Savings %s] Solde: %.2f%n", a1.getAccountNumber(), a1.getBalance());
            System.out.printf("[Credit %s] Solde: %.2f%n", a2.getAccountNumber(), a2.getBalance());

            System.out.println("\nHistorique du SavingsAccount :");
            for (var t : a1.transactions) {  
                System.out.println(t);
            }

            System.out.println("\nHistorique du CreditAccount :");
            for (var t : a2.transactions) {  
                System.out.println(t);
            }

        } catch (BusinessRuleViolation e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
