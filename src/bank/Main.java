package bank;
import bank.HelloBank;

public class Main {

	public static void main(String[] args) {

		SavingsAccount a1 = new SavingsAccount("SA-1001", 200.0, 0.018);
		Account a2 = new CreditAccount("CA-9001", 0.0, 500.0);
		try {
			a1.deposit(50);
			a1.withdraw(20);
			a2.withdraw(100);
			
			a1.applyInterest();
			
			System.out.printf("[Savings %s] Solde: %.2f%n",a1.getAccountNumber(), a1.getBalance());
			System.out.printf("[Credit %s] Solde %.2f%n",a2.getAccountNumber(),a2.getBalance());
			
            System.out.println("\nHistorique du SavingsAccount :");
            for (var t : a1.transactions) {  
                System.out.println(t);
            }
            
            System.out.println("\nHistorique du SavingsAccount :");
            for (var t : a2.transactions) {  
                System.out.println(t);
            }//simplifie ce for
			
		} catch (BusinessRuleViolation e) {
			System.out.println("Erreur : " + e.getMessage());
		}
		
	}
	
	

}
