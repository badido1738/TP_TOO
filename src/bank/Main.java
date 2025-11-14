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
			
		} catch (BusinessRuleViolation e) {
			System.out.println("Erreur : " + e.getMessage());
		}
		
	}
	
	

}
