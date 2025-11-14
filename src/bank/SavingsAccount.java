package bank;

public final class SavingsAccount extends Account{
	
	private final double interestRate;
	
	public SavingsAccount(String id, double initial, double interestRate) {
		super(id,initial);
		this.interestRate = interestRate;
	}
	
	public void applyInterest() {
		balance += balance*interestRate;
		// TODO (TP2): log this as a Transaction ( type = INTEREST )	
	}
	
	@Override
	public void withdraw (double amount) {
		if(amount<0) {
			throw new BusinessRuleViolation("Amount has to be positif");
		}
		if(balance-amount<0) {
			throw new BusinessRuleViolation("Your balance is not enough");
		}
		balance = balance - amount;


	}
}
